package shop.chaekmate.search.task;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.await;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import shop.chaekmate.search.common.EventType;
import shop.chaekmate.search.consumer.BookConsumer;
import shop.chaekmate.search.document.Book;
import shop.chaekmate.search.dto.BaseBookTaskDto;
import shop.chaekmate.search.dto.BookDeleteRequest;
import shop.chaekmate.search.dto.BookInfoRequest;
import shop.chaekmate.search.dto.TaskMapping;
import shop.chaekmate.search.service.BookIndexService;
import shop.chaekmate.search.task.queue.BookTaskQueue;

@SpringBootTest
@ActiveProfiles("test")
class BookTaskTest {
    @Autowired
    BookConsumer consumer;
    @MockitoBean
    BookIndexService bookIndexService;

    @MockitoSpyBean(name = "bookEventQueue")
    BookTaskQueue<TaskMapping<?>> eventQueue;

    @MockitoSpyBean(name = "bookEmbeddingQueue")
    BookTaskQueue<TaskMapping<?>> embeddingQueue;

    @MockitoSpyBean(name = "bookSaveQueue")
    BookTaskQueue<TaskMapping<?>> saveQueue;
    @MockitoSpyBean(name = "bookDeleteQueue")
    BookTaskQueue<TaskMapping<?>> deleteQueue;
    private TaskMapping<?> insertEvent;
    private TaskMapping<?> deleteEvent;
    TaskMapping<?> updateEvent;
    @BeforeEach()
    void init(){
        BookInfoRequest bookInfoRequestInsert = new BookInfoRequest();
        BookInfoRequest bookInfoRequestUpdate = new BookInfoRequest();
        BookDeleteRequest bookDeleteRequestDelete = new BookDeleteRequest();
        bookInfoRequestInsert.setPrice(10000);
        bookInfoRequestInsert.setId(1);
        bookInfoRequestInsert.setPrice(2);
        bookInfoRequestInsert.setDescription("test");
        bookInfoRequestInsert.setBookImages(List.of());
        bookInfoRequestInsert.setAuthor("test");
        bookInfoRequestInsert.setCategories(List.of());
        bookInfoRequestInsert.setTags(List.of());
        bookInfoRequestInsert.setPublicationDatetime(LocalDateTime.now());
        bookInfoRequestInsert.setTitle("test");

        bookInfoRequestUpdate.setPrice(10000);
        bookInfoRequestUpdate.setId(1);
        bookInfoRequestUpdate.setPrice(2);
        bookInfoRequestUpdate.setDescription("test");
        bookInfoRequestUpdate.setBookImages(List.of());
        bookInfoRequestUpdate.setAuthor("test");
        bookInfoRequestUpdate.setCategories(List.of());
        bookInfoRequestUpdate.setTags(List.of());
        bookInfoRequestUpdate.setPublicationDatetime(LocalDateTime.now());
        bookInfoRequestUpdate.setTitle("test");
        Book book1 = Book.builder().id(1).bookImages(List.of()).author("test").categories(List.of()).description("test").embedding(new Float[]{0.1F,}).price(10000).publicationDatetime(LocalDateTime.now()).tags(List.of()).title("test").build();
        Book book2 = Book.builder().id(1).bookImages(List.of()).author("updatetest").categories(List.of()).description("updatetest").embedding(new Float[]{0.1F,}).price(10000).publicationDatetime(LocalDateTime.now()).tags(List.of()).title("updatetest").build();

        bookDeleteRequestDelete.setId(1);
        this.insertEvent = new TaskMapping<>(EventType.INSERT,bookInfoRequestInsert);
        this.updateEvent = new TaskMapping<>(EventType.UPDATE,bookInfoRequestUpdate);
        this.deleteEvent = new TaskMapping<>(EventType.DELETE,bookDeleteRequestDelete);
        when(bookIndexService.insert(any())).thenReturn(book1);
        when(bookIndexService.update(any())).thenReturn(book2);
        doNothing().when(bookIndexService).delete(any());
        doNothing().when(bookIndexService).saveAll(any());

        eventQueue.clear();
        embeddingQueue.clear();
        saveQueue.clear();
    }
    @Test
    void 컨슈머는_데이터를_이벤트큐에넣음(){
        consumer.consume((TaskMapping<BaseBookTaskDto>) insertEvent);
        consumer.consume((TaskMapping<BaseBookTaskDto>) updateEvent);
        consumer.consume((TaskMapping<BaseBookTaskDto>) deleteEvent);
        consumer.consume2((TaskMapping<BaseBookTaskDto>) insertEvent);
        consumer.consume3((TaskMapping<BaseBookTaskDto>) insertEvent);
        verify(eventQueue, timeout(5000).times(5)).offer(any());
        verify(eventQueue, timeout(5000).atLeast(5)).take();
        await().atMost(5, SECONDS).until(() -> eventQueue.getSize() == 0);

    }
    @Test
    void insert_이벤트는_임베딩큐를_거쳐_세이브큐에서_저장(){
        consumer.consume((TaskMapping<BaseBookTaskDto>) insertEvent);
        verify(eventQueue, timeout(5000).times(1)).offer(any());
        verify(eventQueue, timeout(5000).atLeast(1)).take();

        verify(embeddingQueue, timeout(5000).times(1)).offer(any());
        verify(embeddingQueue, timeout(5000).atLeast(1)).take();

        verify(deleteQueue,times(0)).offer(any());

        verify(saveQueue,times(1)).offer(any());
        verify(saveQueue, timeout(5000).atLeast(1)).poll();

    }
    @Test
    void update_이벤트는_임베딩큐를_거쳐_세이브큐에_저장(){

        consumer.consume((TaskMapping<BaseBookTaskDto>) updateEvent);
        verify(eventQueue, timeout(5000).times(1)).offer(any());
        verify(eventQueue, timeout(5000).atLeast(1)).take();

        verify(embeddingQueue, timeout(5000).times(1)).offer(any());
        verify(embeddingQueue, timeout(5000).atLeast(1)).take();

        verify(deleteQueue,times(0)).offer(any());

        verify(saveQueue,times(1)).offer(any());
        verify(saveQueue, timeout(5000).atLeast(1)).poll();
    }
    @Test
    void delete_이벤트는_딜리트큐에서_삭제(){
        consumer.consume((TaskMapping<BaseBookTaskDto>) deleteEvent);
        verify(eventQueue, timeout(5000).times(1)).offer(any());
        verify(eventQueue, timeout(5000).atLeast(1)).take();

        verify(deleteQueue, timeout(5000).times(1)).offer(any());
        verify(deleteQueue, timeout(5000).atLeast(1)).take();

        verify(embeddingQueue,times(0)).offer(any());
        verify(saveQueue,times(0)).offer(any());
    }




}
package shop.chaekmate.search.task;

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
import shop.chaekmate.search.task.worker.BookWaitingTask;

import java.time.LocalDate;
import java.util.List;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.await;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@SpringBootTest
@ActiveProfiles("test")
class BookTaskTest {
    @Autowired
    BookConsumer consumer;
    @MockitoBean
    BookIndexService bookIndexService;
    @MockitoSpyBean
    BookWaitingTask bookWaitingTask;
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
        bookInfoRequestInsert.setBookImages("test");
        bookInfoRequestInsert.setAuthor("test");
        bookInfoRequestInsert.setCategories(List.of());
        bookInfoRequestInsert.setTags(List.of());
        bookInfoRequestInsert.setPublicationDatetime(LocalDate.now());
        bookInfoRequestInsert.setTitle("test");

        bookInfoRequestUpdate.setPrice(10000);
        bookInfoRequestUpdate.setId(1);
        bookInfoRequestUpdate.setPrice(2);
        bookInfoRequestUpdate.setDescription("test");
        bookInfoRequestUpdate.setBookImages("tset");
        bookInfoRequestUpdate.setAuthor("test");
        bookInfoRequestUpdate.setCategories(List.of());
        bookInfoRequestUpdate.setTags(List.of());
        bookInfoRequestUpdate.setPublicationDatetime(LocalDate.now());
        bookInfoRequestUpdate.setTitle("test");
        Book book1 = Book.builder().id(1).bookImages("tset").author("test").categories(List.of()).description("test").embedding(new Float[]{0.1F,}).price(10000).publicationDatetime(LocalDate.now()).tags(List.of()).title("test").build();
        Book book2 = Book.builder().id(1).bookImages("tset").author("updatetest").categories(List.of()).description("updatetest").embedding(new Float[]{0.1F,}).price(10000).publicationDatetime(LocalDate.now()).tags(List.of()).title("updatetest").build();

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
        bookWaitingTask.clear();
    }
    @Test
    void 컨슈머는_데이터를_이벤트큐에넣음(){
        consumer.consume((TaskMapping<BaseBookTaskDto>) insertEvent);
        consumer.consume((TaskMapping<BaseBookTaskDto>) updateEvent);
        consumer.consume((TaskMapping<BaseBookTaskDto>) deleteEvent);
        verify(eventQueue, timeout(10000).times(5)).offer(any());
        verify(eventQueue, timeout(10000).atLeast(3)).take();


        await().atMost(3, SECONDS).until(() -> eventQueue.getSize() == 0);

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


    @Test
    void 여러_이벤트가_순서대로_처리되는지검증(){
        consumer.consume((TaskMapping<BaseBookTaskDto>) insertEvent);
        consumer.consume((TaskMapping<BaseBookTaskDto>) updateEvent);
        consumer.consume((TaskMapping<BaseBookTaskDto>) deleteEvent);

        await().atMost(10, SECONDS).untilAsserted(() -> {
            verify(bookWaitingTask, times(3)).put(any());
            verify(bookWaitingTask, times(3)).poll(anyLong());
        });

    }




}

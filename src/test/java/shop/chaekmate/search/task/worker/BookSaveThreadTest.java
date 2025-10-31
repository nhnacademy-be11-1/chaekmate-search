package shop.chaekmate.search.task.worker;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import shop.chaekmate.search.document.Book;
import shop.chaekmate.search.dto.TaskMapping;
import shop.chaekmate.search.service.BookIndexService;
import shop.chaekmate.search.task.executor.BookTaskExecutor;
import shop.chaekmate.search.task.executor.BookTaskSave;
import shop.chaekmate.search.task.executor.TaskExecutorRegistry;
import shop.chaekmate.search.task.queue.BookTaskQueue;

@ExtendWith(MockitoExtension.class)
class BookSaveThreadTest {

    @Mock
    BookIndexService bookIndexService;
    @Mock
    BookTaskQueue<TaskMapping<Book>> queue;
    BookSaveThread saveThread;
    List<Book> bookList;
    @BeforeEach
    void setUp() {
        List<BookTaskExecutor<?, ?>> executors  = List.of(new BookTaskSave(bookIndexService))    ;

        saveThread = new BookSaveThread(queue, new TaskExecutorRegistry(executors));
        ReflectionTestUtils.setField(saveThread, "batchSize", 3);
        this.bookList= (List<Book>) ReflectionTestUtils.getField(saveThread,"buffer");
    }

    @Test
    void 배치사이즈_도달시_saveAll호출() {
        for (int i = 0; i < 3; i++) {
            bookList.add(Book.builder().build());
        }
        doNothing().when(bookIndexService).saveAll(any());
        ReflectionTestUtils.invokeMethod(saveThread, "saveAll", bookList);
        Mockito.verify(bookIndexService).saveAll(any());
        assertTrue(bookList.isEmpty());
    }

    @Test
    void null_들어왔을때_크기가만족하지않을때saveAll호출() {
        bookList.add(Book.builder().build());
        doNothing().when(bookIndexService).saveAll(any());
        ReflectionTestUtils.invokeMethod(saveThread, "saveAll", bookList);
        Mockito.verify(bookIndexService).saveAll(any());
        assertTrue(bookList.isEmpty());
    }
}

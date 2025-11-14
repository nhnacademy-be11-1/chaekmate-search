package shop.chaekmate.search.task.worker.pool;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import shop.chaekmate.search.document.Book;
import shop.chaekmate.search.dto.BookInfoRequest;
import shop.chaekmate.search.dto.TaskMapping;
import shop.chaekmate.search.task.executor.TaskExecutorRegistry;
import shop.chaekmate.search.task.queue.BookTaskQueue;
import shop.chaekmate.search.task.queue.BookTaskQueueRegistry;
import shop.chaekmate.search.task.worker.BookEmbeddingThread;
import shop.chaekmate.search.task.worker.BookWaitingTask;
import shop.chaekmate.search.task.worker.setting.BookSetting;

import java.util.concurrent.ExecutorService;

@Slf4j
@Component
@RequiredArgsConstructor
public class BookEmbeddingThreadPool {
    private final ExecutorService bookEmbeddingExecutor;
    private final BookTaskQueue<TaskMapping<BookInfoRequest>> bookTaskQueue;
    private final BookTaskQueueRegistry<TaskMapping<Book>> bookTaskQueueRegistry;
    private final TaskExecutorRegistry taskExecutorRegistry;
    private final BookSetting bookEmbeddingSetting;
    private final BookWaitingTask bookWaitingTask;
    @PostConstruct
    public void start() {
        for (int i = 0; i < bookEmbeddingSetting.threadSize(); i++) {
            bookEmbeddingExecutor.submit(new BookEmbeddingThread(bookTaskQueue,bookTaskQueueRegistry,taskExecutorRegistry,bookWaitingTask));
        }
    }

}

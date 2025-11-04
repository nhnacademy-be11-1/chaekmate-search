package shop.chaekmate.search.task.worker.pool;

import jakarta.annotation.PostConstruct;
import java.util.concurrent.ExecutorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import shop.chaekmate.search.dto.BookInfoRequest;
import shop.chaekmate.search.dto.TaskMapping;
import shop.chaekmate.search.task.executor.TaskExecutorRegistry;
import shop.chaekmate.search.task.queue.BookTaskQueue;
import shop.chaekmate.search.task.queue.BookTaskQueueRegistry;
import shop.chaekmate.search.task.worker.BookEmbeddingThread;
import shop.chaekmate.search.task.worker.setting.BookSetting;

@Slf4j
@Component
@RequiredArgsConstructor
public class BookEmbeddingThreadPool {
    private final ExecutorService bookEmbeddingExecutor;
    private final BookTaskQueue<TaskMapping<BookInfoRequest>> bookTaskQueue;
    private final BookTaskQueueRegistry<?> bookTaskQueueRegistry;
    private final TaskExecutorRegistry taskExecutorRegistry;
    private final BookSetting bookEmbeddingSetting;
    @PostConstruct
    public void start() {
        for (int i = 0; i < bookEmbeddingSetting.threadSize(); i++) {
            bookEmbeddingExecutor.submit(new BookEmbeddingThread(bookTaskQueue,bookTaskQueueRegistry,taskExecutorRegistry));
        }
    }

}

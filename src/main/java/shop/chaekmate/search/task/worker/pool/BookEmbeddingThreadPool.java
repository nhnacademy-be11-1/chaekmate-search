package shop.chaekmate.search.task.worker.pool;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import shop.chaekmate.search.dto.BookInfoRequest;
import shop.chaekmate.search.dto.TaskMapping;
import shop.chaekmate.search.task.executor.TaskExecutorRegistry;
import shop.chaekmate.search.task.queue.BookTaskQueue;
import shop.chaekmate.search.task.queue.BookTaskQueueRegistry;
import shop.chaekmate.search.task.worker.BookEmbeddingThread;
import shop.chaekmate.search.task.worker.setting.BookEmbeddingSetting;

@Slf4j
@Component
@RequiredArgsConstructor
public class BookEmbeddingThreadPool {
    private static final String THREAD_NAME = "BookEmbeddingThread-";
    private final BookEmbeddingSetting bookEmbeddingSetting;
    private final BookTaskQueue<TaskMapping<BookInfoRequest>> bookTaskQueue;
    private final BookTaskQueueRegistry bookTaskQueueRegistry;
    private final TaskExecutorRegistry taskExecutorRegistry;

    public synchronized void start() {
        for (int i = 0; i < bookEmbeddingSetting.getWorkers(); i++) {
            Thread thread = new Thread(new BookEmbeddingThread(bookTaskQueue, bookTaskQueueRegistry, taskExecutorRegistry));
            thread.setName(String.format("%s%d", THREAD_NAME, i + 1));
            log.info("{}", thread.getName());
            thread.start();
        }
    }

}

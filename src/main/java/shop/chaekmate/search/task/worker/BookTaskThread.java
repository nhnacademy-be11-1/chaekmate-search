package shop.chaekmate.search.task.worker;

import lombok.extern.slf4j.Slf4j;
import shop.chaekmate.search.dto.TaskMapping;
import shop.chaekmate.search.task.executor.BookTaskExecutor;
import shop.chaekmate.search.task.executor.TaskExecutorRegistry;
import shop.chaekmate.search.task.queue.BookTaskQueue;

import java.util.concurrent.TimeUnit;

@Slf4j
public class BookTaskThread implements Runnable {
    private final BookTaskQueue bookTaskQueue;
    private final TaskExecutorRegistry taskExecutorRegistry;
    private final BookTaskThreadPool bookTaskThreadPool;

    public BookTaskThread(BookTaskQueue bookTaskQueue, TaskExecutorRegistry taskExecutorRegistry, BookTaskThreadPool bookTaskThreadPool) {
        this.bookTaskQueue = bookTaskQueue;
        this.taskExecutorRegistry = taskExecutorRegistry;
        this.bookTaskThreadPool = bookTaskThreadPool;
    }

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                TaskMapping task = bookTaskQueue.poll();
                if (task == null) {
                    if (bookTaskThreadPool.getCurrentThreadCount() > bookTaskThreadPool.getBaseThreadCount()) {
                        bookTaskThreadPool.decrementThreadCount();
                        break;
                    } else {
                        continue;
                    }
                }
                BookTaskExecutor executor = taskExecutorRegistry.get(task.getEventType());
                executor.execute(task.getBaseBookTaskDto());
            } catch (Exception e) {
                log.error("Book task error", e);
            }
        }
    }
}

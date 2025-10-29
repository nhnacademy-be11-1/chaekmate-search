package shop.chaekmate.search.task.worker;

import lombok.extern.slf4j.Slf4j;
import shop.chaekmate.search.common.EventType;
import shop.chaekmate.search.dto.BookDeleteRequest;
import shop.chaekmate.search.dto.TaskMapping;
import shop.chaekmate.search.task.executor.BookTaskExecutor;
import shop.chaekmate.search.task.executor.TaskExecutorRegistry;
import shop.chaekmate.search.task.queue.BookTaskQueue;

@Slf4j
public class BookDeleteThread implements Runnable {
    private final BookTaskQueue<TaskMapping<BookDeleteRequest>> bookTaskQueue;
    private final BookTaskExecutor<TaskMapping<BookDeleteRequest>, Void> task;

    public BookDeleteThread(BookTaskQueue<TaskMapping<BookDeleteRequest>> bookTaskQueue, TaskExecutorRegistry taskExecutorRegistry) {
        this.task = taskExecutorRegistry.get(EventType.DELETE);
        this.bookTaskQueue = bookTaskQueue;
    }

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                TaskMapping<BookDeleteRequest> mapping = bookTaskQueue.take();
                task.execute(mapping);
            } catch (Exception e) {
                log.error("Book delete thread error", e);
            }
        }
    }
}

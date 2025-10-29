package shop.chaekmate.search.task.worker;

import lombok.extern.slf4j.Slf4j;
import shop.chaekmate.search.dto.TaskMapping;
import shop.chaekmate.search.task.queue.BookTaskQueue;
import shop.chaekmate.search.task.queue.BookTaskQueueRegistry;

@Slf4j
public class BookEventThread implements Runnable {
    private final BookTaskQueue<?> bookTaskQueue;
    private final BookTaskQueueRegistry bookTaskQueueRegistry;
    public BookEventThread(BookTaskQueue<?> bookTaskQueue, BookTaskQueueRegistry bookTaskQueueRegistry) {
        this.bookTaskQueue = bookTaskQueue;
        this.bookTaskQueueRegistry = bookTaskQueueRegistry;
    }
    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                TaskMapping<?> task = (TaskMapping<?>) bookTaskQueue.take();
                BookTaskQueue<?> nextQueue = bookTaskQueueRegistry.getQueue(task.getEventType());
                ((BookTaskQueue<TaskMapping<?>>) nextQueue).offer(task);
            } catch (Exception e) {
                log.error("Book event thread error", e);
            }
        }
    }
}

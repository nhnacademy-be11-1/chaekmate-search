package shop.chaekmate.search.task.worker;

import lombok.extern.slf4j.Slf4j;
import shop.chaekmate.search.dto.TaskMapping;
import shop.chaekmate.search.task.executor.BookTaskExecutor;
import shop.chaekmate.search.task.executor.TaskExecutorRegistry;
import shop.chaekmate.search.task.queue.BookEventQueue;
import shop.chaekmate.search.task.queue.BookTaskQueue;

@Slf4j
public class BookEventThread implements Runnable {
    private final BookEventQueue bookEventQueue;
    private final TaskQueueRegistry taskQueueRegistry;

    public BookEventThread(BookEventQueue bookEventQueue, TaskQueueRegistry taskQueueRegistry) {
        this.bookEventQueue = bookEventQueue;
        this.taskQueueRegistry = taskQueueRegistry;
    }
    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                TaskMapping<?> task = bookEventQueue.poll();
                BookTaskQueue nextQueue = taskQueueRegistry.get(task.getEventType());
                nextQueue.offer(task);
            } catch (Exception e) {
                log.error("Book event error", e);
            }
        }
    }
}

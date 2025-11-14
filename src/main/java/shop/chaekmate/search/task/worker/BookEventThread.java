package shop.chaekmate.search.task.worker;

import lombok.extern.slf4j.Slf4j;
import shop.chaekmate.search.dto.BaseBookTaskDto;
import shop.chaekmate.search.dto.TaskMapping;
import shop.chaekmate.search.task.queue.BookTaskQueue;
import shop.chaekmate.search.task.queue.BookTaskQueueRegistry;

@Slf4j
public class BookEventThread implements Runnable {
    private final BookTaskQueue<TaskMapping<?>> bookTaskQueue;
    private final BookTaskQueueRegistry<TaskMapping<?>> bookTaskQueueRegistry;
    private final BookWaitingTask waitingTask;
    public BookEventThread(BookTaskQueue<TaskMapping<?>> bookTaskQueue, BookTaskQueueRegistry<TaskMapping<?>> bookTaskQueueRegistry, BookWaitingTask waitingTask) {
        this.bookTaskQueue = bookTaskQueue;
        this.bookTaskQueueRegistry = bookTaskQueueRegistry;
        this.waitingTask = waitingTask;
    }
    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                TaskMapping<?> task = bookTaskQueue.take();
                if(waitingTask.peek((TaskMapping<? extends BaseBookTaskDto>) task)){
                    BookTaskQueue<TaskMapping<?>> nextQueue = bookTaskQueueRegistry.getQueue(task.getEventType());
                    nextQueue.offer(task);
                }
            } catch (Exception e) {
                log.error("Book event thread error", e);
            }
        }
    }
}

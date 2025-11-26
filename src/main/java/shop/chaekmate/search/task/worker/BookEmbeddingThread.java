package shop.chaekmate.search.task.worker;

import lombok.extern.slf4j.Slf4j;
import shop.chaekmate.search.document.Book;
import shop.chaekmate.search.dto.BookInfoRequest;
import shop.chaekmate.search.dto.TaskMapping;
import shop.chaekmate.search.task.executor.BookTaskExecutor;
import shop.chaekmate.search.task.executor.TaskExecutorRegistry;
import shop.chaekmate.search.task.queue.BookTaskQueue;
import shop.chaekmate.search.task.queue.BookTaskQueueRegistry;

@Slf4j
public class BookEmbeddingThread implements Runnable {
    private final BookTaskQueue<TaskMapping<BookInfoRequest>> bookTaskQueue;
    private final BookTaskQueueRegistry<TaskMapping<Book>> bookTaskQueueRegistry;
    private final TaskExecutorRegistry taskExecutorRegistry;
    private final BookWaitingTask bookWaitingTask;

    public BookEmbeddingThread(BookTaskQueue<TaskMapping<BookInfoRequest>> bookTaskQueue,
                               BookTaskQueueRegistry<TaskMapping<Book>> bookTaskQueueRegistry, TaskExecutorRegistry taskExecutorRegistry, BookWaitingTask bookWaitingTask) {
        this.bookTaskQueue = bookTaskQueue;
        this.bookTaskQueueRegistry = bookTaskQueueRegistry;
        this.taskExecutorRegistry = taskExecutorRegistry;
        this.bookWaitingTask = bookWaitingTask;
    }

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            TaskMapping<BookInfoRequest> task = bookTaskQueue.take();
            try {
                BookTaskExecutor<TaskMapping<BookInfoRequest>, TaskMapping<Book>> bookTaskExecutor = taskExecutorRegistry.get(
                        task.getEventType());
                TaskMapping<Book> nextTask = bookTaskExecutor.execute(task);
                BookTaskQueue<TaskMapping<Book>> nextQueue = bookTaskQueueRegistry.getQueue(nextTask.getEventType());
                nextQueue.offer(nextTask);
            }catch (Exception e){
                bookWaitingTask.poll(task.getTaskData().getId());
                log.error("Book embedding thread error", e);
            }
        }
    }
}

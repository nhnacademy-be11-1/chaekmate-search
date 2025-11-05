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
    private final BookTaskQueueRegistry bookTaskQueueRegistry;
    private final TaskExecutorRegistry taskExecutorRegistry;

    public BookEmbeddingThread(BookTaskQueue<TaskMapping<BookInfoRequest>> bookTaskQueue,
                               BookTaskQueueRegistry bookTaskQueueRegistry, TaskExecutorRegistry taskExecutorRegistry) {
        this.bookTaskQueue = bookTaskQueue;
        this.bookTaskQueueRegistry = bookTaskQueueRegistry;
        this.taskExecutorRegistry = taskExecutorRegistry;
    }

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                TaskMapping<BookInfoRequest> task = bookTaskQueue.take();
                BookTaskExecutor<TaskMapping<BookInfoRequest>, TaskMapping<Book>> bookTaskExecutor = taskExecutorRegistry.get(
                        task.getEventType());
                TaskMapping<Book> taskMapping = bookTaskExecutor.execute(task);
                BookTaskQueue<TaskMapping<Book>> nextQueue = bookTaskQueueRegistry.getQueue(taskMapping.getEventType());
                nextQueue.offer(taskMapping);
            } catch (Exception e) {
                log.error("Book embedding thread error", e);
            }
        }
    }
}

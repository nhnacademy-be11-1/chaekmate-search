package shop.chaekmate.search.task.worker;

import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import shop.chaekmate.search.common.EventType;
import shop.chaekmate.search.document.Book;
import shop.chaekmate.search.dto.TaskMapping;
import shop.chaekmate.search.event.UpdateGroupEvent;
import shop.chaekmate.search.task.executor.BookTaskExecutor;
import shop.chaekmate.search.task.executor.TaskExecutorRegistry;
import shop.chaekmate.search.task.queue.BookTaskQueue;

@Slf4j
public class BookSaveThread implements Runnable {
    private final BookTaskQueue<TaskMapping<Book>> bookTaskQueue;
    private final  int batchSize = 500;
    private final BookTaskExecutor<List<Book>, Void> task;
    private final List<Book> buffer = new ArrayList<>();
    private final BookWaitingTask bookWaitingTask;
    private final ApplicationEventPublisher publisher;

    public BookSaveThread(BookTaskQueue<TaskMapping<Book>> bookTaskQueue, TaskExecutorRegistry taskExecutorRegistry, BookWaitingTask bookWaitingTask, ApplicationEventPublisher publisher) {
        this.bookTaskQueue = bookTaskQueue;
        this.task = taskExecutorRegistry.get(EventType.SAVE);
        this.bookWaitingTask = bookWaitingTask;
        this.publisher = publisher;
    }

    @Override
    public void run() {

        try {
            while (!Thread.currentThread().isInterrupted()) {
                TaskMapping<Book> mapping = bookTaskQueue.poll();
                if (mapping != null) {
                    buffer.add(mapping.getTaskData());
                }
                boolean sizeTrigger = buffer.size() >= batchSize;
                boolean timeoutTrigger = mapping == null && !buffer.isEmpty();

                if (sizeTrigger || timeoutTrigger) {
                    saveAll(buffer);
                }
            }
        } catch (Exception e) {
            log.error("Book save thread error", e);
        } finally {
            saveAll(buffer);
        }
    }

    private void saveAll(List<Book> buffer) {
        if (buffer.isEmpty()) {
            return;
        }
        try {
            task.execute(buffer);
            for (Book book : buffer) {
                bookWaitingTask.poll(book.getId());
                publisher.publishEvent(new UpdateGroupEvent(book));
            }
            buffer.clear();
        } catch (Exception e) {
            log.error("Book save thread error", e);
        }
    }

}

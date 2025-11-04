package shop.chaekmate.search.task.worker;

import lombok.extern.slf4j.Slf4j;
import shop.chaekmate.search.common.EventType;
import shop.chaekmate.search.document.Book;
import shop.chaekmate.search.dto.TaskMapping;
import shop.chaekmate.search.task.executor.BookTaskExecutor;
import shop.chaekmate.search.task.executor.TaskExecutorRegistry;
import shop.chaekmate.search.task.queue.BookTaskQueue;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public class BookSaveThread implements Runnable {
    private final BookTaskQueue<TaskMapping<Book>> bookTaskQueue;
    private static final int DEFAULT_BACTH_SIZE = 500;
    private final BookTaskExecutor<List<Book>, Void> task;
    private final List<Book> buffer = new ArrayList<>();

    public BookSaveThread(BookTaskQueue<TaskMapping<Book>> bookTaskQueue, TaskExecutorRegistry taskExecutorRegistry) {
        this.bookTaskQueue = bookTaskQueue;
        this.task = taskExecutorRegistry.get(EventType.SAVE);
    }

    @Override
    public void run() {

        try {
            while (!Thread.currentThread().isInterrupted()) {
                TaskMapping<Book> mapping = bookTaskQueue.poll();
                if (mapping != null) {
                    buffer.add(mapping.getTaskData());
                }
                boolean sizeTrigger = buffer.size() >= DEFAULT_BACTH_SIZE;
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
            buffer.clear();
        } catch (Exception e) {
            log.error("Book save thread error", e);
        }
    }

}

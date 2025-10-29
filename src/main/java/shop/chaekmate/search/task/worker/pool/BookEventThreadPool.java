package shop.chaekmate.search.task.worker.pool;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import shop.chaekmate.search.dto.TaskMapping;
import shop.chaekmate.search.task.queue.BookTaskQueue;
import shop.chaekmate.search.task.queue.BookTaskQueueRegistry;
import shop.chaekmate.search.task.worker.BookEventThread;
import shop.chaekmate.search.task.worker.setting.BookEventSetting;

@Slf4j
@Component
public class BookEventThreadPool {
    private static final String THREAD_NAME = "BookEventThread-";
    private final BookEventSetting bookEventSetting;
    private final BookTaskQueue<TaskMapping<?>> bookTaskQueue;
    private final BookTaskQueueRegistry bookTaskQueueRegistry;

    public BookEventThreadPool(BookEventSetting bookEventSetting, @Qualifier("bookEventQueue")BookTaskQueue<TaskMapping<?>> bookTaskQueue, BookTaskQueueRegistry bookTaskQueueRegistry) {
        this.bookEventSetting = bookEventSetting;
        this.bookTaskQueue = bookTaskQueue;
        this.bookTaskQueueRegistry = bookTaskQueueRegistry;
    }

    public synchronized void start() {
        for (int i = 0; i < bookEventSetting.getWorkers(); i++) {
            Thread thread = new Thread(new BookEventThread(bookTaskQueue, bookTaskQueueRegistry));
            thread.setName(String.format("%s%d", THREAD_NAME, i + 1));
            log.info("{}",thread.getName());
            thread.start();
        }
    }
}
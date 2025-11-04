package shop.chaekmate.search.task.worker.pool;

import jakarta.annotation.PostConstruct;
import java.util.concurrent.ExecutorService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import shop.chaekmate.search.dto.TaskMapping;
import shop.chaekmate.search.task.queue.BookTaskQueue;
import shop.chaekmate.search.task.queue.BookTaskQueueRegistry;
import shop.chaekmate.search.task.worker.BookEventThread;
import shop.chaekmate.search.task.worker.setting.BookSetting;
@Component
public class BookEventThreadPool {
    private final ExecutorService bookEventExecutor;
    private final BookTaskQueue<TaskMapping<?>> bookTaskQueue;
    private final BookTaskQueueRegistry<?> bookTaskQueueRegistry;
    private final BookSetting bookEventSetting;

    public BookEventThreadPool(ExecutorService bookEventExecutor,
                               @Qualifier("bookEventQueue") BookTaskQueue<TaskMapping<?>> bookTaskQueue,
                               BookTaskQueueRegistry<?> bookTaskQueueRegistry, BookSetting bookEventSetting) {
        this.bookEventExecutor = bookEventExecutor;
        this.bookTaskQueue = bookTaskQueue;
        this.bookTaskQueueRegistry = bookTaskQueueRegistry;
        this.bookEventSetting = bookEventSetting;
    }

    @PostConstruct
    public void start() {
        for (int i = 0; i < bookEventSetting.threadSize(); i++) {
            bookEventExecutor.submit(new BookEventThread(bookTaskQueue, bookTaskQueueRegistry));
        }
    }

}
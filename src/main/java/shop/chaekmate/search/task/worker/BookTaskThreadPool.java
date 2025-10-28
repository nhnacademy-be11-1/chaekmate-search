package shop.chaekmate.search.task.worker;

import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import shop.chaekmate.search.task.executor.TaskExecutorRegistry;
import shop.chaekmate.search.task.queue.BookTaskQueue;
import shop.chaekmate.search.task.queue.event.QueueSizeChangedEvent;
import shop.chaekmate.search.task.worker.setting.BookTaskSetting;

import java.util.concurrent.atomic.AtomicInteger;

@Component
@RequiredArgsConstructor
public class BookTaskThreadPool {
    private static final String THREAD_NAME = "BookTaskThread-";
    private final TaskExecutorRegistry taskExecutorRegistry;
    private final AtomicInteger currentThreadCount = new AtomicInteger(0);
    private final BookTaskQueue bookTaskQueue;
    private final BookTaskSetting bookTaskSetting;

    public synchronized void start() {
        for (int i = 0; i < bookTaskSetting.getBaseWorkers(); i++) {
            Thread thread = new Thread(new BookTaskThread(bookTaskQueue, taskExecutorRegistry, this));
            thread.setName(String.format("%s%d", THREAD_NAME, i + 1));
            thread.start();
        }
        currentThreadCount.addAndGet(bookTaskSetting.getBaseWorkers());
    }

    @EventListener(QueueSizeChangedEvent.class)
    public void queueSizeChangeEvent() {
        if (getCurrentThreadCount() < bookTaskSetting.getMaxWorkers()) {
            addThread();
        }
    }

    public void addThread() {
        Thread thread = new Thread(new BookTaskThread(bookTaskQueue, taskExecutorRegistry, this));
        thread.setName(String.format("%s%d", THREAD_NAME, currentThreadCount.incrementAndGet()));
        thread.start();
    }

    public int getCurrentThreadCount() {
        return this.currentThreadCount.get();
    }

    public int getBaseThreadCount() {
        return bookTaskSetting.getBaseWorkers();
    }

    public void decrementThreadCount() {
        currentThreadCount.decrementAndGet();
    }
}

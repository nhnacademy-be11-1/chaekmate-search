package shop.chaekmate.search.task.worker;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import shop.chaekmate.search.task.executor.TaskExecutorRegistry;
import shop.chaekmate.search.task.queue.BookTaskQueue;

import java.util.concurrent.atomic.AtomicInteger;

@Component
@RequiredArgsConstructor
public class BookTaskThreadPool {
    private static final String THREAD_NAME = "BookTaskThread-";
    private static final Integer DEFAULT_THREAD_COUNT_MAX = 100;
    private static final Integer DEFAULT_THREAD_COUNT_MIN = 5;
    private final TaskExecutorRegistry taskExecutorRegistry;
    private final AtomicInteger currentThreadCount = new AtomicInteger(0);

    public synchronized void start(BookTaskQueue bookTaskQueue) {
        for (int i = 0; i < DEFAULT_THREAD_COUNT_MIN; i++) {
            Thread thread = new Thread(new BookTaskThread(bookTaskQueue, taskExecutorRegistry, this));
            thread.setName(String.format("%s%d", THREAD_NAME, i + 1));
            thread.start();
        }
        currentThreadCount.addAndGet(DEFAULT_THREAD_COUNT_MIN);
    }

    public synchronized void addThread(BookTaskQueue bookTaskQueue) {
        Thread thread = new Thread(new BookTaskThread(bookTaskQueue, taskExecutorRegistry, this));
        thread.setName(String.format("%s%d", THREAD_NAME, currentThreadCount.incrementAndGet()));
        thread.start();
    }

    public int getCurrentThreadCount() {
        return this.currentThreadCount.get();
    }

    public static Integer getDefaultThreadCountMax() {
        return DEFAULT_THREAD_COUNT_MAX;
    }

    public static Integer getDefaultThreadCountMin() {
        return DEFAULT_THREAD_COUNT_MIN;
    }

    public void decrementThreadCount() {
        currentThreadCount.decrementAndGet();
    }
}

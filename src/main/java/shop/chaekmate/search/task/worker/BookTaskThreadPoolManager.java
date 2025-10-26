package shop.chaekmate.search.task.worker;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import shop.chaekmate.search.task.queue.BookTaskQueue;

@Component
@RequiredArgsConstructor
public class BookTaskThreadPoolManager {
    private final BookTaskThreadPool bookTaskThreadPool;
    private final BookTaskQueue bookTaskQueue;

    public void checkStatus(int size) {
        int min = BookTaskThreadPool.getDefaultThreadCountMin();
        int max = BookTaskThreadPool.getDefaultThreadCountMax();
        int current = bookTaskThreadPool.getCurrentThreadCount();
        int targetThreads = Math.min(max, Math.max(min, size / 2000));
        if (current < targetThreads) {
            bookTaskThreadPool.addThread(bookTaskQueue);
        }
    }
}

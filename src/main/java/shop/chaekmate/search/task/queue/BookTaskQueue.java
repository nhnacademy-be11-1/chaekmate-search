package shop.chaekmate.search.task.queue;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import shop.chaekmate.search.dto.TaskMapping;
import shop.chaekmate.search.task.worker.BookTaskThreadPoolManager;

@Component
@RequiredArgsConstructor
public class BookTaskQueue {
    BlockingQueue<TaskMapping> taskQueue = new LinkedBlockingQueue<>();
    private final BookTaskThreadPoolManager bookTaskThreadPoolManager;
    public void orderOffer(TaskMapping taskMapping) {
        taskQueue.offer(taskMapping);
        bookTaskThreadPoolManager.checkStatus(taskQueue.size(),this);
    }

    public TaskMapping poll(int time, TimeUnit timeUnit) {
        try {
            return taskQueue.poll(time,timeUnit);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}

package shop.chaekmate.search.task.queue;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import org.springframework.stereotype.Component;
import shop.chaekmate.search.task.executor.TaskMapping;

@Component
public class BookTaskQueue {
    BlockingQueue<TaskMapping> taskQueue = new LinkedBlockingQueue<>();

    public void orderOffer(TaskMapping taskMapping) {
        taskQueue.offer(taskMapping);
    }

    public TaskMapping orderPoll() throws InterruptedException {
        return taskQueue.take();
    }

}

package shop.chaekmate.search.task.queue;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import shop.chaekmate.search.dto.TaskMapping;
import shop.chaekmate.search.task.queue.setting.TaskQueueTimeSetting;

@Component
@RequiredArgsConstructor
public class BookEventQueue {
    BlockingQueue<TaskMapping<?>> taskQueue = new LinkedBlockingQueue<>();
    private final TaskQueueTimeSetting taskQueueTimeSetting;

    public void offer(TaskMapping<?> taskMapping) {
        taskQueue.offer(taskMapping);
    }

    public TaskMapping<?> poll() {
        try {
            return taskQueue.take();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}

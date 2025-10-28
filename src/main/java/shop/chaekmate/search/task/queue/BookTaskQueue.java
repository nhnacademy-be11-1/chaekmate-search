package shop.chaekmate.search.task.queue;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import shop.chaekmate.search.dto.TaskMapping;
import shop.chaekmate.search.task.queue.event.QueueSizeChangedEvent;
import shop.chaekmate.search.task.queue.setting.TaskQueueTimeSetting;

@Component
@RequiredArgsConstructor
public class BookTaskQueue {
    BlockingQueue<TaskMapping> taskQueue = new LinkedBlockingQueue<>();
    private final TaskQueueTimeSetting taskQueueTimeSetting;
    private final ApplicationEventPublisher publisher;


    public void orderOffer(TaskMapping taskMapping) {
        taskQueue.offer(taskMapping);
        if (taskQueue.size() % 500 == 0) {
            publisher.publishEvent(new QueueSizeChangedEvent());
        }
    }

    public TaskMapping poll() {
        try {
            return taskQueue.poll(taskQueueTimeSetting.getTime(), taskQueueTimeSetting.getTimeUnit());
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}

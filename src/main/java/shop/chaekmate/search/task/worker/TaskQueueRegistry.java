package shop.chaekmate.search.task.worker;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Component;
import shop.chaekmate.search.common.EventType;
import shop.chaekmate.search.task.queue.BookTaskQueue;

@Component
public class TaskQueueRegistry {
    private final Map<EventType, BookTaskQueue> registry;

    public TaskQueueRegistry(List<BookTaskQueue> queues) {
        this.registry = new HashMap<>();
        for (BookTaskQueue queue : queues) {
            for (EventType type : queue.getSupportedTypes()) {
                registry.put(type, queue);
            }
        }
    }

    public BookTaskQueue get(EventType type) {
        return registry.get(type);
    }
}

package shop.chaekmate.search.task.executor;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import shop.chaekmate.search.common.EventType;

@Component
@RequiredArgsConstructor
public class TaskExecutorRegistry {
    private final Map<EventType, BookTaskExecutor> registry = new EnumMap<>(EventType.class);

    public TaskExecutorRegistry(List<BookTaskExecutor> executors) {
        for (BookTaskExecutor executor : executors) {
            if (executor instanceof BookTaskInsert) {
                registry.put(EventType.INSERT, executor);
            }
            if (executor instanceof BookTaskUpdate) {
                registry.put(EventType.UPDATE, executor);
            }
            if (executor instanceof BookTaskDelete) {
                registry.put(EventType.DELETE, executor);
            }
        }
    }

    public BookTaskExecutor get(EventType type) {
        return registry.get(type);
    }
}

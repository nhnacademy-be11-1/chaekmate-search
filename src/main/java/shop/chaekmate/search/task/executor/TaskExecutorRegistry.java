package shop.chaekmate.search.task.executor;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import shop.chaekmate.search.common.EventType;

@Component
public class TaskExecutorRegistry {
    private final Map<EventType, BookTaskExecutor<?>> registry;

    public TaskExecutorRegistry(List<BookTaskExecutor<?>> executors) {
        this.registry = executors.stream()
                .collect(Collectors.toMap(BookTaskExecutor::getType, Function.identity()));
    }

    public BookTaskExecutor<?> get(EventType type) {
        return registry.get(type);
    }
}

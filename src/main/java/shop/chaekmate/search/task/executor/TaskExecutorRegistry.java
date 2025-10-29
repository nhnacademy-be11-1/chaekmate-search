package shop.chaekmate.search.task.executor;

import org.springframework.stereotype.Component;
import shop.chaekmate.search.common.EventType;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
@Component
public class TaskExecutorRegistry {
    private final Map<EventType, BookTaskExecutor<?, ?>> registry;

    public TaskExecutorRegistry(List<BookTaskExecutor<?, ?>> executors) {
        this.registry = executors.stream()
                .collect(Collectors.toMap(BookTaskExecutor::getType, Function.identity()));
    }

    public <T, R> BookTaskExecutor<T, R> get(EventType type) {
        return (BookTaskExecutor<T, R>) registry.get(type);
    }
}
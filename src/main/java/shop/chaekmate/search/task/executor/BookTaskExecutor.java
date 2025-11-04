package shop.chaekmate.search.task.executor;

import shop.chaekmate.search.common.EventType;

public interface BookTaskExecutor<T, R> {
    R execute(T mapping);
    EventType getType();
}

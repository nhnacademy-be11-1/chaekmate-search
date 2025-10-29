package shop.chaekmate.search.task.executor;

import shop.chaekmate.search.common.EventType;
import shop.chaekmate.search.dto.BaseBookTaskDto;
import shop.chaekmate.search.dto.TaskMapping;

public interface BookTaskExecutor<T> {
    T execute(BaseBookTaskDto bookTaskDto);

    EventType getType();
}

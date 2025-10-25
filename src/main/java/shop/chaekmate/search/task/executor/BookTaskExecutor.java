package shop.chaekmate.search.task.executor;

import shop.chaekmate.search.common.EventType;
import shop.chaekmate.search.dto.BaseBookTaskDto;

public interface BookTaskExecutor {
    void execute(BaseBookTaskDto bookTaskDto);
    EventType getType();
}

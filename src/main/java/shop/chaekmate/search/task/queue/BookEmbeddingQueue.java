package shop.chaekmate.search.task.queue;

import java.util.Set;
import shop.chaekmate.search.common.EventType;
import shop.chaekmate.search.dto.TaskMapping;

public class BookEmbeddingQueue implements BookTaskQueue{
    @Override
    public void offer(TaskMapping<?> taskMapping) {

    }

    @Override
    public TaskMapping<?> poll() {
        return null;
    }

    @Override
    public Set<EventType> getSupportedTypes() {
        return Set.of(EventType.INSERT,EventType.UPDATE);
    }
}

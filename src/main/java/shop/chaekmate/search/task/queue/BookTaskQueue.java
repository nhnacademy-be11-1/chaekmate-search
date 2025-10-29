package shop.chaekmate.search.task.queue;

import java.util.Set;
import shop.chaekmate.search.common.EventType;
import shop.chaekmate.search.dto.TaskMapping;

public interface BookTaskQueue {
    public void offer(TaskMapping<?> taskMapping);
    public TaskMapping<?> poll();
    public Set<EventType> getSupportedTypes();
}

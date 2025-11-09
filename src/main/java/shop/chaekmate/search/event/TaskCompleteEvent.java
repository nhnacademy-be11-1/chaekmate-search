package shop.chaekmate.search.event;

import shop.chaekmate.search.dto.TaskMapping;

public record TaskCompleteEvent(TaskMapping<?> taskMapping) {
}

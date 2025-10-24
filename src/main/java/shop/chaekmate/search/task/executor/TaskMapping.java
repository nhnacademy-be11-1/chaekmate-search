package shop.chaekmate.search.task.executor;

import lombok.Getter;
import shop.chaekmate.search.common.EventType;
import shop.chaekmate.search.dto.BookInfoRequest;
@Getter
public class TaskMapping {
    public EventType eventType;
    public BookInfoRequest bookInfoRequest;
}

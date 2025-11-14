package shop.chaekmate.search.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import shop.chaekmate.search.common.EventType;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Setter
public class TaskMapping<T> {
    public EventType eventType;
    public T taskData;
}

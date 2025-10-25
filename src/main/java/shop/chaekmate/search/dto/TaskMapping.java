package shop.chaekmate.search.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import shop.chaekmate.search.common.EventType;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class TaskMapping {
    public EventType eventType;
    public BaseBookTaskDto baseBookTaskDto;
}

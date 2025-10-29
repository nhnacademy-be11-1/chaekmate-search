package shop.chaekmate.search.task.worker.setting;

import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
public class BookDeleteSetting {
    private final int workers = 2;
}

package shop.chaekmate.search.task.worker.setting;

import lombok.Getter;
import org.springframework.stereotype.Component;

@Component
@Getter
public class BookTaskSetting {
    private final int maxWorkers = 16;
    private final int baseWorkers = 4;
}

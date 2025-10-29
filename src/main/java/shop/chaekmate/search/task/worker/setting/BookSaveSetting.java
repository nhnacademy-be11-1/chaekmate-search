package shop.chaekmate.search.task.worker.setting;

import lombok.Getter;
import org.springframework.stereotype.Component;

@Component
@Getter
public class BookSaveSetting {
    private final int workers = 2;
}

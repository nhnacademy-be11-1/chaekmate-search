package shop.chaekmate.search.task.queue.setting;

import lombok.Getter;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
@Getter
public class TaskQueueTimeSetting {
    private final TimeUnit timeUnit = TimeUnit.SECONDS;
    private final int time = 30;

}

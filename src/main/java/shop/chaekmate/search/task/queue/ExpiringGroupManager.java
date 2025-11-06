package shop.chaekmate.search.task.queue;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import shop.chaekmate.search.document.ExpiringGroup;

import javax.annotation.PostConstruct;
import java.time.Duration;
import java.util.UUID;
import java.util.concurrent.DelayQueue;

@Slf4j
@Component
@RequiredArgsConstructor
public class ExpiringGroupManager {
    private final DelayQueue<ExpiringGroup> queue = new DelayQueue<>();
    private final ApplicationEventPublisher publisher;

    public void offer(UUID id, Duration ttl) {
        queue.offer(new ExpiringGroup(id, ttl.minusMinutes(3)));
    }

    @PostConstruct
    public void start() {
        Thread t = new Thread(() -> {
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    ExpiringGroup task = queue.take();
                    publisher.publishEvent(task);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        });
        t.setDaemon(true);
        t.setName("ExpiringGroupManager");
        t.start();
    }
}

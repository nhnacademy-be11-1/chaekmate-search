package shop.chaekmate.search.task.queue;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.await;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.time.Duration;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.test.util.ReflectionTestUtils;
import shop.chaekmate.search.document.ExpiringGroup;

@ExtendWith(MockitoExtension.class)
class ExpiringGroupManagerTest {
    @Mock
    ApplicationEventPublisher publisher;

    @InjectMocks
    ExpiringGroupManager expiringGroupManager;

    @BeforeEach
    void init(){
        expiringGroupManager.start();
    }
    @Test
    void take후_이벤트발행(){
        doNothing().when(publisher).publishEvent(any(ExpiringGroup.class));
        System.out.println("publisher mock: " + publisher);
        System.out.println("manager.publisher: " + ReflectionTestUtils.getField(expiringGroupManager, "publisher"));

        UUID uuid = UUID.randomUUID();
        expiringGroupManager.offer(uuid ,Duration.ofMinutes(3).plusSeconds(1));
        await().atMost(5, SECONDS).untilAsserted(() -> verify(publisher, times(1)).publishEvent(any(ExpiringGroup.class)));
    }

}
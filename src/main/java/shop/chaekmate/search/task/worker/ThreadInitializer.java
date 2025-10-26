package shop.chaekmate.search.task.worker;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import shop.chaekmate.search.task.queue.BookTaskQueue;

@Component
@RequiredArgsConstructor

public class ThreadInitializer implements ApplicationRunner {
    private final ApplicationContext applicationContext;
    @Override
    public void run(ApplicationArguments args) {
        applicationContext.getBean(BookTaskThreadPool.class).start(applicationContext.getBean(BookTaskQueue.class));
    }
}

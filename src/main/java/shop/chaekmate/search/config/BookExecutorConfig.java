package shop.chaekmate.search.config;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import shop.chaekmate.common.log.executor.LogThreadPoolExecutor;
import shop.chaekmate.search.task.worker.setting.BookDeleteSetting;
import shop.chaekmate.search.task.worker.setting.BookEmbeddingSetting;
import shop.chaekmate.search.task.worker.setting.BookEventSetting;
import shop.chaekmate.search.task.worker.setting.BookSaveSetting;
import shop.chaekmate.search.task.worker.setting.BookSetting;

@Configuration
public class BookExecutorConfig {
    static final String EVENT_TYPE = "THREAD";
    @Value("${spring.application.name:localhost}")
    private String serviceName;

    @Bean(destroyMethod = "shutdown")
    public ExecutorService bookEventExecutor(BookSetting bookEventSetting) {
        return executor(bookEventSetting);
    }

    @Bean(destroyMethod = "shutdown")
    public ExecutorService bookDeleteExecutor(BookSetting bookDeleteSetting) {
        return executor(bookDeleteSetting);
    }

    @Bean(destroyMethod = "shutdown")
    public ExecutorService bookEmbeddingExecutor(BookSetting bookEmbeddingSetting) {
        return executor(bookEmbeddingSetting);
    }

    @Bean(destroyMethod = "shutdown")
    public ExecutorService bookSaveExecutor(BookSetting bookSaveSetting) {
        return executor(bookSaveSetting);
    }

    @Bean
    public BookSetting bookDeleteSetting() {
        return new BookDeleteSetting();
    }

    @Bean
    public BookSetting bookEventSetting() {
        return new BookEventSetting();
    }

    @Bean
    public BookSetting bookEmbeddingSetting() {
        return new BookEmbeddingSetting();
    }

    @Bean
    public BookSetting bookSaveSetting() {
        return new BookSaveSetting();
    }

    private ExecutorService executor(BookSetting bookSetting) {
        LogThreadPoolExecutor executor =
                new LogThreadPoolExecutor(
                        bookSetting.threadSize(),
                        bookSetting.threadSize(),
                        0L,
                        TimeUnit.MILLISECONDS,
                        new LinkedBlockingQueue<>(),
                        String.format("%s:%s:%s", serviceName, EVENT_TYPE, bookSetting.name())
                );
        AtomicInteger count = new AtomicInteger(1);

        executor.setThreadFactory(r -> {
            Thread t = new Thread(r);
            t.setName(bookSetting.name() + "-" + count.getAndIncrement());
            t.setDaemon(false);
            return t;
        });
        return executor;

    }
}

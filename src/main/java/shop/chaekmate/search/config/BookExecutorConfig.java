package shop.chaekmate.search.config;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import shop.chaekmate.search.task.worker.setting.BookDeleteSetting;
import shop.chaekmate.search.task.worker.setting.BookEmbeddingSetting;
import shop.chaekmate.search.task.worker.setting.BookEventSetting;
import shop.chaekmate.search.task.worker.setting.BookSaveSetting;
import shop.chaekmate.search.task.worker.setting.BookSetting;

@Configuration
public class BookExecutorConfig {
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
        AtomicInteger count = new AtomicInteger(1);
        return Executors.newFixedThreadPool(bookSetting.threadSize(), r -> {
            Thread t = new Thread(r);
            t.setName(bookSetting.name() + "-" + count.getAndIncrement());
            t.setDaemon(false);
            return t;
        });
    }
}

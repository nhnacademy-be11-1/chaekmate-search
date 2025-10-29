package shop.chaekmate.search.task.worker;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import shop.chaekmate.search.task.worker.pool.BookDeleteThreadPool;
import shop.chaekmate.search.task.worker.pool.BookEmbeddingThreadPool;
import shop.chaekmate.search.task.worker.pool.BookEventThreadPool;
import shop.chaekmate.search.task.worker.pool.BookSaveThreadPool;

@Component
@RequiredArgsConstructor

public class ThreadInitializer implements ApplicationRunner {
    private final BookEventThreadPool bookEventThreadPool;
    private final BookEmbeddingThreadPool bookEmbeddingThreadPool;
    private final BookSaveThreadPool bookSaveThreadPool;
    private final BookDeleteThreadPool bookDeleteThreadPool;
    @Override
    public void run(ApplicationArguments args) {
        bookEventThreadPool.start();
        bookEmbeddingThreadPool.start();
        bookSaveThreadPool.start();
        bookDeleteThreadPool.start();
    }
}

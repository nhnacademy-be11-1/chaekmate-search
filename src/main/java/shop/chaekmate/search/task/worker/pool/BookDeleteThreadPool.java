package shop.chaekmate.search.task.worker.pool;

import jakarta.annotation.PostConstruct;
import java.util.concurrent.ExecutorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import shop.chaekmate.search.dto.BookDeleteRequest;
import shop.chaekmate.search.dto.TaskMapping;
import shop.chaekmate.search.task.executor.TaskExecutorRegistry;
import shop.chaekmate.search.task.queue.BookTaskQueue;
import shop.chaekmate.search.task.worker.BookDeleteThread;
import shop.chaekmate.search.task.worker.setting.BookSetting;

@Slf4j
@Component
@RequiredArgsConstructor
public class BookDeleteThreadPool {
    private final ExecutorService bookDeleteExecutor;
    private final BookTaskQueue<TaskMapping<BookDeleteRequest>> bookTaskQueue;
    private final TaskExecutorRegistry taskExecutorRegistry;
    private final BookSetting bookDeleteSetting;

    @PostConstruct
    public void start() {
        for (int i = 0; i < bookDeleteSetting.threadSize(); i++) {
            bookDeleteExecutor.submit(new BookDeleteThread(bookTaskQueue,taskExecutorRegistry));
        }
    }

}

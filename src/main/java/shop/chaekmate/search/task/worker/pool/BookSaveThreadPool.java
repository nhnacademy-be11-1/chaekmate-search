package shop.chaekmate.search.task.worker.pool;

import jakarta.annotation.PostConstruct;
import java.util.concurrent.ExecutorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import shop.chaekmate.search.document.Book;
import shop.chaekmate.search.dto.TaskMapping;
import shop.chaekmate.search.task.executor.TaskExecutorRegistry;
import shop.chaekmate.search.task.queue.BookTaskQueue;
import shop.chaekmate.search.task.worker.BookSaveThread;
import shop.chaekmate.search.task.worker.setting.BookSetting;

@Slf4j
@Component
@RequiredArgsConstructor
public class BookSaveThreadPool {
    private final ExecutorService bookSaveExecutor;
    private final BookTaskQueue<TaskMapping<Book>> bookTaskQueue;
    private final TaskExecutorRegistry taskExecutorRegistry;
    private final BookSetting bookSaveSetting;
    @PostConstruct
    public void start() {
        for (int i = 0; i < bookSaveSetting.threadSize(); i++) {
            bookSaveExecutor.submit(new BookSaveThread(bookTaskQueue,taskExecutorRegistry));
        }
    }
}

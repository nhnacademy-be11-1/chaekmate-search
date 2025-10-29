package shop.chaekmate.search.task.worker.pool;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import shop.chaekmate.search.document.Book;
import shop.chaekmate.search.dto.TaskMapping;
import shop.chaekmate.search.task.executor.TaskExecutorRegistry;
import shop.chaekmate.search.task.queue.BookTaskQueue;
import shop.chaekmate.search.task.worker.BookSaveThread;
import shop.chaekmate.search.task.worker.setting.BookSaveSetting;

@Slf4j
@Component
@RequiredArgsConstructor
public class BookSaveThreadPool {
    private static final String THREAD_NAME = "BookSaveThread-";
    private final BookSaveSetting bookSaveSetting;
    private final BookTaskQueue<TaskMapping<Book>> bookTaskQueue;
    private final TaskExecutorRegistry taskExecutorRegistry;

    public synchronized void start() {
        for (int i = 0; i < bookSaveSetting.getWorkers(); i++) {
            Thread thread = new Thread(new BookSaveThread(bookTaskQueue,taskExecutorRegistry));
            thread.setName(String.format("%s%d", THREAD_NAME, i + 1));
            log.info("{}",thread.getName());
            thread.start();
        }
    }
}

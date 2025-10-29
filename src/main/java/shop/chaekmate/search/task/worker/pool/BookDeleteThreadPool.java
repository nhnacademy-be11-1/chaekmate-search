package shop.chaekmate.search.task.worker.pool;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import shop.chaekmate.search.dto.BookDeleteRequest;
import shop.chaekmate.search.dto.TaskMapping;
import shop.chaekmate.search.task.executor.TaskExecutorRegistry;
import shop.chaekmate.search.task.queue.BookTaskQueue;
import shop.chaekmate.search.task.worker.BookDeleteThread;
import shop.chaekmate.search.task.worker.setting.BookDeleteSetting;

@Slf4j
@Component
@RequiredArgsConstructor
public class BookDeleteThreadPool {
    private static final String THREAD_NAME = "BookDeleteThread-";
    private final BookDeleteSetting bookDeleteSetting;
    private final BookTaskQueue<TaskMapping<BookDeleteRequest>> bookTaskQueue;
    private final TaskExecutorRegistry taskExecutorRegistry;

    public synchronized void start() {
        for (int i = 0; i < bookDeleteSetting.getWorkers(); i++) {
            Thread thread = new Thread(new BookDeleteThread(bookTaskQueue,taskExecutorRegistry));
            thread.setName(String.format("%s%d", THREAD_NAME, i + 1));
            log.info("{}",thread.getName());
            thread.start();
        }
    }

}

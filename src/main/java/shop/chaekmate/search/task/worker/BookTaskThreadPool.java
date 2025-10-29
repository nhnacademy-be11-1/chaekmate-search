package shop.chaekmate.search.task.worker;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import shop.chaekmate.search.task.queue.BookEventQueue;
import shop.chaekmate.search.task.worker.setting.BookTaskSetting;

@Component
@RequiredArgsConstructor
public class BookTaskThreadPool {
    private static final String THREAD_NAME = "BookEventThread-";
    private final BookEventQueue bookEventQueue;
    private final BookTaskSetting bookTaskSetting;
    private final TaskQueueRegistry taskQueueRegistry;

    public synchronized void start() {
        for (int i = 0; i < bookTaskSetting.getBaseWorkers(); i++) {
            Thread thread = new Thread(new BookEventThread(bookEventQueue, taskQueueRegistry));
            thread.setName(String.format("%s%d", THREAD_NAME, i + 1));
            thread.start();
        }
    }
}
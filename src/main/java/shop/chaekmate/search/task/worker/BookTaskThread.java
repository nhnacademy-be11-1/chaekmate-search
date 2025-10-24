package shop.chaekmate.search.task.worker;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import shop.chaekmate.search.task.executor.BookTaskExecutor;
import shop.chaekmate.search.task.executor.TaskExecutorRegistry;
import shop.chaekmate.search.task.executor.TaskMapping;
import shop.chaekmate.search.task.queue.BookTaskQueue;

@Component
@RequiredArgsConstructor
@Slf4j
public class BookTaskThread implements Runnable {
    private final BookTaskQueue bookTaskQueue;
    private final TaskExecutorRegistry taskExecutorRegistry;
    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                TaskMapping task = bookTaskQueue.orderPoll();
                BookTaskExecutor taskExecutor = taskExecutorRegistry.get(task.getEventType());
                taskExecutor.execute(task.getBookInfoRequest());
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } catch (Exception e) {
                log.error("Book task error", e);
            }
        }
    }
}

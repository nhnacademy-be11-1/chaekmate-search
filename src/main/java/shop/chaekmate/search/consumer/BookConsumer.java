package shop.chaekmate.search.consumer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import shop.chaekmate.search.dto.BaseBookTaskDto;
import shop.chaekmate.search.dto.TaskMapping;
import shop.chaekmate.search.task.queue.BookTaskQueue;
import shop.chaekmate.search.task.worker.BookWaitingTask;

@Component
@Slf4j
public class BookConsumer {
    private final BookWaitingTask bookWaitingTask;
    private final BookTaskQueue<TaskMapping<?>> bookEventQueue;
    public BookConsumer(BookWaitingTask bookWaitingTask, @Qualifier("bookEventQueue") BookTaskQueue<TaskMapping<?>> bookEventQueue) {
        this.bookWaitingTask = bookWaitingTask;
        this.bookEventQueue = bookEventQueue;
    }
    @RabbitListener(queues = "cm-book-1")
    public void consume(TaskMapping<BaseBookTaskDto> taskMapping) {
        bookWaitingTask.put(taskMapping);
        bookEventQueue.offer(taskMapping);
    }
}

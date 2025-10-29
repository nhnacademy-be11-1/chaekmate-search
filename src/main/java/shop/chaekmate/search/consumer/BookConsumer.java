package shop.chaekmate.search.consumer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import shop.chaekmate.search.dto.BaseBookTaskDto;
import shop.chaekmate.search.dto.TaskMapping;
import shop.chaekmate.search.task.queue.BookEventQueue;

@Component
@Slf4j
@RequiredArgsConstructor
public class BookConsumer {
    private final BookEventQueue bookEventQueue;

    @RabbitListener(queues = "cm-book-1")
    public void consume(TaskMapping<BaseBookTaskDto> taskMapping) {
        bookEventQueue.offer(taskMapping);
    }

    @RabbitListener(queues = "cm-book-2")
    public void consume2(TaskMapping<BaseBookTaskDto> taskMapping) {
        bookEventQueue.offer(taskMapping);
    }

    @RabbitListener(queues = "cm-book-3")
    public void consume3(TaskMapping<BaseBookTaskDto> taskMapping) {
        bookEventQueue.offer(taskMapping);

    }

}

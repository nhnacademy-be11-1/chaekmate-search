package shop.chaekmate.search.consumer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import shop.chaekmate.search.dto.BookInfoRequest;

@Component
@Slf4j

public class BookConsumer {
    @RabbitListener(queues = "cm-book-1")
    public void consume(BookInfoRequest bookInfoRequest) {
    }
    @RabbitListener(queues = "cm-book-2")
    public void consume2(BookInfoRequest bookInfoRequest) {
    }
    @RabbitListener(queues = "cm-book-3")
    public void consume3(BookInfoRequest bookInfoRequest) {

    }

}

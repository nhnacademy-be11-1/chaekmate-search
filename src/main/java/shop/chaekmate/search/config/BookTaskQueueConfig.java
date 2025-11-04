package shop.chaekmate.search.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import shop.chaekmate.search.common.EventType;
import shop.chaekmate.search.document.Book;
import shop.chaekmate.search.dto.BookDeleteRequest;
import shop.chaekmate.search.dto.BookInfoRequest;
import shop.chaekmate.search.dto.TaskMapping;
import shop.chaekmate.search.task.queue.BookTaskQueue;

import java.util.LinkedHashSet;
import java.util.Set;

@Configuration
public class BookTaskQueueConfig {

    @Bean(name = "bookEventQueue")
    public BookTaskQueue<TaskMapping<?>> bookEventQueue() {
        return new BookTaskQueue<>(new LinkedHashSet<>());
    }

    @Bean
    public BookTaskQueue<TaskMapping<BookInfoRequest>> bookEmbeddingQueue() {
        return new BookTaskQueue<>(Set.of(EventType.INSERT, EventType.UPDATE));
    }

    @Bean
    public BookTaskQueue<TaskMapping<Book>> bookSaveQueue() {
        return new BookTaskQueue<>(Set.of(EventType.SAVE));
    }

    @Bean
    public BookTaskQueue<TaskMapping<BookDeleteRequest>> bookDeleteQueue() {
        return new BookTaskQueue<>(Set.of(EventType.DELETE));
    }
}
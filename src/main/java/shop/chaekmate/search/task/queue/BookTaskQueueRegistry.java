package shop.chaekmate.search.task.queue;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import shop.chaekmate.search.common.EventType;
import shop.chaekmate.search.document.Book;
import shop.chaekmate.search.dto.BookDeleteRequest;
import shop.chaekmate.search.dto.BookInfoRequest;
import shop.chaekmate.search.dto.TaskMapping;

@Component
public class BookTaskQueueRegistry<T> {
    private final Map<EventType, BookTaskQueue<T>> registry = new EnumMap<>(EventType.class);

    public BookTaskQueueRegistry(List<BookTaskQueue<T>> queues) {
        for (BookTaskQueue<T> queue : queues) {
            for (EventType bookTaskQueue : queue.getSupportEventType()) {
                registry.put(bookTaskQueue, queue);
            }
        }
    }

    public BookTaskQueue<T> getQueue(EventType type) {
        return registry.get(type);
    }
}
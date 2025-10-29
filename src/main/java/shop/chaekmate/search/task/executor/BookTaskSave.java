package shop.chaekmate.search.task.executor;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import shop.chaekmate.search.common.EventType;
import shop.chaekmate.search.document.Book;
import shop.chaekmate.search.service.BookIndexService;

import java.util.List;
@Component
@RequiredArgsConstructor
public class BookTaskSave implements BookTaskExecutor<List<Book>,Void>{
    private final BookIndexService bookIndexService;

    @Override
    public Void execute(List<Book> mapping) {
        return bookIndexService.saveAll(mapping);
    }

    @Override
    public EventType getType() {
        return EventType.SAVE;
    }
}

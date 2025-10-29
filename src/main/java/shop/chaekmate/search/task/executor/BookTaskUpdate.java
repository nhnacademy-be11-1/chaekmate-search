package shop.chaekmate.search.task.executor;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import shop.chaekmate.search.common.EventType;
import shop.chaekmate.search.document.Book;
import shop.chaekmate.search.dto.BaseBookTaskDto;
import shop.chaekmate.search.dto.BookInfoRequest;
import shop.chaekmate.search.dto.TaskMapping;
import shop.chaekmate.search.service.BookIndexService;

@Component
@RequiredArgsConstructor
public class BookTaskUpdate implements BookTaskExecutor<TaskMapping<BookInfoRequest>,TaskMapping<Book
        >> {
    private final BookIndexService bookIndexService;

    @Override
    public TaskMapping<Book> execute(TaskMapping<BookInfoRequest> mapping) {
        return new TaskMapping<>(EventType.SAVE,bookIndexService.update(mapping.getTaskData())) ;
    }

    @Override
    public EventType getType() {
        return EventType.UPDATE;
    }
}

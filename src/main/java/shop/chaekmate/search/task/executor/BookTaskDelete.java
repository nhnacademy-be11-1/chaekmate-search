package shop.chaekmate.search.task.executor;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import shop.chaekmate.search.common.EventType;
import shop.chaekmate.search.dto.BaseBookTaskDto;
import shop.chaekmate.search.dto.BookDeleteRequest;
import shop.chaekmate.search.dto.BookInfoRequest;
import shop.chaekmate.search.dto.TaskMapping;
import shop.chaekmate.search.service.BookIndexService;
@Component
@RequiredArgsConstructor
public class BookTaskDelete implements BookTaskExecutor<TaskMapping<BookDeleteRequest>,Void> {
    private final BookIndexService bookIndexService;

    @Override
    public Void execute(TaskMapping<BookDeleteRequest> mapping) {
        bookIndexService.delete(mapping.getTaskData());
        return null;
    }

    @Override
    public EventType getType() {
        return EventType.DELETE;
    }
}

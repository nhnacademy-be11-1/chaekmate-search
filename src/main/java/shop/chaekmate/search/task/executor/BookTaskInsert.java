package shop.chaekmate.search.task.executor;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import shop.chaekmate.search.dto.BookInfoRequest;
import shop.chaekmate.search.service.BookIndexService;

@Component
@RequiredArgsConstructor
public class BookTaskInsert implements BookTaskExecutor {
    private final BookIndexService bookIndexService;

    @Override
    public void execute(BookInfoRequest bookInfoRequest) {
        bookIndexService.delete(bookInfoRequest);
    }
}

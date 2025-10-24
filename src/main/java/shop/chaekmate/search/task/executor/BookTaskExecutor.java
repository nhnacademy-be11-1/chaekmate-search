package shop.chaekmate.search.task.executor;

import shop.chaekmate.search.dto.BookInfoRequest;

public interface BookTaskExecutor {
    void execute(BookInfoRequest bookInfoRequest);
}

package shop.chaekmate.search.consumer;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import shop.chaekmate.search.common.EventType;
import shop.chaekmate.search.dto.BaseBookTaskDto;
import shop.chaekmate.search.dto.BookInfoRequest;
import shop.chaekmate.search.dto.TaskMapping;
import shop.chaekmate.search.task.queue.BookTaskQueue;
import shop.chaekmate.search.task.worker.BookWaitingTask;

@ExtendWith(MockitoExtension.class)
class BookConsumerTest {
    @Mock
    BookTaskQueue<TaskMapping<?>> bookEventQueue;
    @Mock
    BookWaitingTask bookWaitingTask;
    @InjectMocks
    BookConsumer bookConsumer;

    @Test
    void 컨슘() {
        TaskMapping<BaseBookTaskDto> bookInfoRequest = new TaskMapping<>(EventType.INSERT, new BookInfoRequest());
        bookConsumer.consume(bookInfoRequest);
        verify(bookEventQueue, times(1)).offer(bookInfoRequest);
    }
}
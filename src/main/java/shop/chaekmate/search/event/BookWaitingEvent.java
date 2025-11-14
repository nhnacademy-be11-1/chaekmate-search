package shop.chaekmate.search.event;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import shop.chaekmate.search.dto.TaskMapping;
import shop.chaekmate.search.task.queue.BookTaskQueue;

@Component
public class BookWaitingEvent {
    private final BookTaskQueue<TaskMapping<?>> bookEventQueue;
    public BookWaitingEvent( @Qualifier("bookEventQueue") BookTaskQueue<TaskMapping<?>> bookEventQueue) {
        this.bookEventQueue = bookEventQueue;
    }
    @EventListener(TaskCompleteEvent.class)
    void complete (TaskCompleteEvent taskCompleteEvent){
        bookEventQueue.offer(taskCompleteEvent.taskMapping());
    }
}

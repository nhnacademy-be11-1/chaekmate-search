package shop.chaekmate.search.task.worker;

import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import shop.chaekmate.search.dto.BaseBookTaskDto;
import shop.chaekmate.search.dto.TaskMapping;
import shop.chaekmate.search.event.TaskCompleteEvent;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

@Component
@RequiredArgsConstructor
public class BookWaitingTask {
    private final ApplicationEventPublisher publisher;
    Map<Long, ConcurrentLinkedQueue<TaskMapping<?>>> bookWaitingTaskMap = new ConcurrentHashMap<>();

    public void put(TaskMapping<? extends BaseBookTaskDto> waitingTask) {
        Long bookId = waitingTask.getTaskData().getId();
        ConcurrentLinkedQueue<TaskMapping<?>> queue =
                bookWaitingTaskMap.computeIfAbsent(bookId, id -> new ConcurrentLinkedQueue<>());
        queue.offer(waitingTask);
    }


    public boolean peek(TaskMapping<? extends BaseBookTaskDto> task) {
        Long id = task.getTaskData().getId();
        if (!bookWaitingTaskMap.containsKey(id)) {
            return false;
        }
        return Objects.requireNonNull(bookWaitingTaskMap.get(id).peek()).equals(task);

    }

    public void poll(long id) {
        if (bookWaitingTaskMap.containsKey(id)) {
            ConcurrentLinkedQueue<TaskMapping<?>> concurrentLinkedQueue = bookWaitingTaskMap.get(id);
            if (concurrentLinkedQueue == null || concurrentLinkedQueue.isEmpty()) {
                return;
            }
            concurrentLinkedQueue.poll();
            if (!concurrentLinkedQueue.isEmpty()) {
                publisher.publishEvent(new TaskCompleteEvent(concurrentLinkedQueue.peek()));
            } else {
                bookWaitingTaskMap.remove(id);
            }
        }
    }
    public void clear(){
        bookWaitingTaskMap.clear();
    }
}

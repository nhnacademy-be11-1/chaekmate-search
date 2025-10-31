package shop.chaekmate.search.task.queue;

import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import lombok.Getter;
import shop.chaekmate.search.common.EventType;

public class BookTaskQueue<T> {

    @Getter
    private final Set<EventType> supportEventType;
    private final BlockingQueue<T> queue = new LinkedBlockingQueue<>();

    public BookTaskQueue(Set<EventType> eventType) {
        this.supportEventType = eventType;
    }

    public void offer(T task) {
        queue.offer(task);
    }

    public T take() {
        try {
            return queue.take();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
    public T poll(){
        try {
            return queue.poll(3, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public int getSize() {
        return queue.size();
    }
    public void clear(){
        queue.clear();
    }
}
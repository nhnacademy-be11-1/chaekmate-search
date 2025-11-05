package shop.chaekmate.search.document;

import lombok.Getter;

import java.time.Duration;
import java.util.UUID;
import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

@Getter
public class ExpiringGroup implements Delayed {
    private final UUID uuid;
    private final long expireAt;

    public ExpiringGroup(UUID uuid, Duration ttl) {
        this.uuid = uuid;
        this.expireAt = System.currentTimeMillis() + ttl.toMillis();
    }

    @Override
    public long getDelay(TimeUnit unit) {
        return unit.convert(expireAt - System.currentTimeMillis(), TimeUnit.MILLISECONDS);
    }

    @Override
    public int compareTo(Delayed o) {
        return Long.compare(this.expireAt, ((ExpiringGroup) o).expireAt);
    }
}

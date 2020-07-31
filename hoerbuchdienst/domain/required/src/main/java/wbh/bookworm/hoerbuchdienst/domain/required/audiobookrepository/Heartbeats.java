package wbh.bookworm.hoerbuchdienst.domain.required.audiobookrepository;

import java.io.Serializable;
import java.time.Instant;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.stream.Collectors;

import io.micronaut.core.annotation.Introspected;

@Introspected
public final class Heartbeats implements Serializable {

    private static final long serialVersionUID = 2579224237511506862L;

    private final transient Object heartbeatLock;

    /**
     * hostname -> Heartbeat
     */
    private final Map<ShardName, Heartbeat> heartbeatMap;

    private final Set<ShardName> lostHeartbeats;

    public Heartbeats() {
        heartbeatLock = new Object();
        heartbeatMap = new ConcurrentHashMap<>(5);
        lostHeartbeats = new ConcurrentSkipListSet<>();
    }

    public Heartbeat remember(final ShardName shardname, final Heartbeat heartbeat) {
        synchronized (heartbeatLock) {
            lostHeartbeats.remove(shardname);
            return heartbeatMap.put(shardname, heartbeat);
        }
    }

    public void forget(final ShardName shardName) {
        synchronized (heartbeatLock) {
            heartbeatMap.remove(shardName);
        }
    }

    public void forgetAll(final Set<ShardName> shardNames) {
        synchronized (heartbeatLock) {
            shardNames.forEach(shardname -> {
                heartbeatMap.remove(shardname);
                lostHeartbeats.add(shardname);
            });
        }
    }

    public boolean someReceived() {
        return !heartbeatMap.isEmpty();
    }

    public int count() {
        return heartbeatMap.size();
    }

    public Map<ShardName, Instant> lastTimestamps() {
        return heartbeatMap.entrySet()
                .stream()
                .map(entry -> Map.entry(entry.getKey(), entry.getValue().getPointInTime()))
                .collect(Collectors.toUnmodifiableMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    public boolean isLost(final ShardName shardname) {
        return lostHeartbeats.contains(shardname);
    }

}

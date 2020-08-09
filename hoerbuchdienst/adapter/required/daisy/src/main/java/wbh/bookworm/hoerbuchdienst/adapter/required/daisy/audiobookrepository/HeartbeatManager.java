package wbh.bookworm.hoerbuchdienst.adapter.required.daisy.audiobookrepository;

import javax.inject.Singleton;
import java.time.Instant;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.stream.Collectors;

import wbh.bookworm.hoerbuchdienst.domain.required.audiobookrepository.Heartbeat;
import wbh.bookworm.hoerbuchdienst.domain.required.audiobookrepository.ShardName;

@Singleton
final class HeartbeatManager {

    private final Object heartbeatLock;

    /**
     * hostname -> Heartbeat
     */
    private final Map<ShardName, Heartbeat> heartbeatMap;

    private final Set<ShardName> lostHeartbeats;

    HeartbeatManager() {
        heartbeatLock = new Object();
        heartbeatMap = new ConcurrentHashMap<>(5);
        lostHeartbeats = new ConcurrentSkipListSet<>();
    }

    Heartbeat remember(final ShardName shardname, final Heartbeat heartbeat) {
        synchronized (heartbeatLock) {
            lostHeartbeats.remove(shardname);
            return heartbeatMap.put(shardname, heartbeat);
        }
    }

    void forget(final ShardName shardName) {
        synchronized (heartbeatLock) {
            heartbeatMap.remove(shardName);
        }
    }

    void forgetAll(final Iterable<ShardName> shardNames) {
        synchronized (heartbeatLock) {
            shardNames.forEach(shardName -> {
                heartbeatMap.remove(shardName);
                lostHeartbeats.add(shardName);
            });
        }
    }

    boolean someReceived() {
        return !heartbeatMap.isEmpty();
    }

    int count() {
        return heartbeatMap.size();
    }

    Map<ShardName, Instant> lastTimestamps() {
        return heartbeatMap.entrySet()
                .stream()
                .map(entry -> Map.entry(entry.getKey(), entry.getValue().getPointInTime()))
                .collect(Collectors.toUnmodifiableMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    boolean isLost(final ShardName shardname) {
        return lostHeartbeats.contains(shardname);
    }

}

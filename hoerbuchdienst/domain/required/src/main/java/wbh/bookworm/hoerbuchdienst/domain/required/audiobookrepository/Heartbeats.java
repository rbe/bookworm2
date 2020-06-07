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

    private final Object heartbeatLock = new Object();

    /**
     * hostname -> Heartbeat
     */
    private final Map<String, Heartbeat> heartbeats;

    private final Set<String> lostHeartbeats;

    public Heartbeats() {
        heartbeats = new ConcurrentHashMap<>(5);
        lostHeartbeats = new ConcurrentSkipListSet<>();
    }

    public Heartbeat remember(final String hostname, final Heartbeat heartbeat) {
        synchronized (heartbeatLock) {
            lostHeartbeats.remove(hostname);
            return heartbeats.put(hostname, heartbeat);
        }
    }

    public void forget(final String hostname) {
        synchronized (heartbeatLock) {
            heartbeats.remove(hostname);
        }
    }

    public void forgetAll(final Set<String> hostnames) {
        synchronized (heartbeatLock) {
            hostnames.forEach(hostname -> {
                heartbeats.remove(hostname);
                lostHeartbeats.add(hostname);
            });
        }
    }

    public boolean someReceived() {
        return !heartbeats.isEmpty();
    }

    public int count() {
        return heartbeats.size();
    }

    public Map<String, Instant> lastTimestamps() {
        return heartbeats.entrySet()
                .stream()
                .map(entry -> Map.entry(entry.getKey(), entry.getValue().getPointInTime()))
                .collect(Collectors.toUnmodifiableMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    public boolean isLost(final String hostname) {
        return lostHeartbeats.contains(hostname);
    }

}

package wbh.bookworm.hoerbuchdienst.domain.required.audiobookrepository;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import io.micronaut.core.annotation.Introspected;

@Introspected
public final class DataHeartbeats implements Serializable {

    private static final long serialVersionUID = 2579224237511506862L;

    /**
     * hostname -> Heartbeat
     */
    private final Map<String, DataHeartbeat> heartbeats;

    public DataHeartbeats() {
        heartbeats = new ConcurrentHashMap<>(5);
    }

    public void remember(final String hostname, final DataHeartbeat dataHeartbeat) {
        heartbeats.put(hostname, dataHeartbeat);
    }

    public void forget(final String hostname) {
        heartbeats.remove(hostname);
    }

    public boolean someReceived() {
        return !heartbeats.isEmpty();
    }

    public int count() {
        return heartbeats.size();
    }

    public List<ShardObject> allShardObjects() {
        return heartbeats.values()
                .stream()
                .map(DataHeartbeat::getShardObjects)
                .flatMap(Collection::stream)
                .collect(Collectors.toUnmodifiableList());
    }

    public Map<String, List<ShardObject>> allShardObjectsByHostname() {
        return heartbeats.entrySet()
                .stream()
                .map(entry -> Map.entry(entry.getKey(), entry.getValue().getShardObjects()))
                .collect(Collectors.toUnmodifiableMap(Map.Entry::getKey, Map.Entry::getValue));
    }

}

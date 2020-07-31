package wbh.bookworm.hoerbuchdienst.domain.required.audiobookrepository;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import io.micronaut.core.annotation.Introspected;

@Introspected
public final class Databeats implements Serializable {

    private static final long serialVersionUID = 2579224237511506862L;

    /**
     * hostname -> Heartbeat
     */
    private final Map<ShardName, Databeat> dataHeartbeatMap;

    public Databeats() {
        dataHeartbeatMap = new ConcurrentHashMap<>(5);
    }

    public void remember(final ShardName shardName, final Databeat databeat) {
        dataHeartbeatMap.put(shardName, databeat);
    }

    public void forget(final ShardName shardName) {
        dataHeartbeatMap.remove(shardName);
    }

    public boolean someReceived() {
        return !dataHeartbeatMap.isEmpty();
    }

    /**
     * Number of received heartbeats
     */
    public int count() {
        return dataHeartbeatMap.size();
    }

    public List<ShardAudiobook> allShardAudiobooks() {
        return dataHeartbeatMap.values()
                .stream()
                .map(Databeat::getShardAudiobooks)
                .flatMap(Collection::stream)
                .collect(Collectors.toUnmodifiableList());
    }

    public Map<ShardName, List<ShardAudiobook>> allShardAudiobooksByHostname() {
        return dataHeartbeatMap.entrySet()
                .stream()
                .map(entry -> Map.entry(entry.getKey(), entry.getValue().getShardAudiobooks()))
                .collect(Collectors.toUnmodifiableMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    /**
     * Summarize sizes of all shard objects
     */
    public Optional<Long> totalSize() {
        return allShardAudiobooks()
                .stream()
                .map(ShardAudiobook::size)
                .reduce(Long::sum);
    }

    public Set<ShardName> allShardNames() {
        return dataHeartbeatMap.keySet();
    }

    @Override
    public String toString() {
        return String.format("DataHeartbeats{dataHeartbeatMap=%s}", dataHeartbeatMap.size());
    }

}

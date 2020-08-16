package wbh.bookworm.hoerbuchdienst.adapter.required.daisy.audiobookrepository;

import javax.inject.Singleton;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import io.micronaut.runtime.event.annotation.EventListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import wbh.bookworm.hoerbuchdienst.domain.required.audiobookrepository.Databeat;
import wbh.bookworm.hoerbuchdienst.domain.required.audiobookrepository.ShardAudiobook;
import wbh.bookworm.hoerbuchdienst.domain.required.audiobookrepository.ShardHighWatermarkEvent;
import wbh.bookworm.hoerbuchdienst.domain.required.audiobookrepository.ShardName;

import aoc.mikrokosmos.crypto.messagedigest.MessageDigester;

@Singleton
final class DatabeatManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(DatabeatManager.class);

    /**
     * ShardName -> Databeat
     */
    private final Map<ShardName, Databeat> databeatMap;

    private final AtomicInteger heartbeatHighWatermark;

    DatabeatManager() {
        databeatMap = new ConcurrentHashMap<>(5);
        heartbeatHighWatermark = new AtomicInteger(0);
    }

    void remember(final ShardName shardName, final Databeat databeat) {
        databeatMap.put(shardName, databeat);
    }

    void forget(final ShardName shardName) {
        databeatMap.remove(shardName);
    }

    boolean someReceived() {
        return !databeatMap.isEmpty();
    }

    /**
     * Number of received databeats
     */
    int numberOfDatabeats() {
        return databeatMap.size();
    }

    long numerOfObjects() {
        return (long) allShardsAudiobooks().size();
    }

    List<ShardAudiobook> allShardsAudiobooks() {
        return databeatMap.values()
                .stream()
                .map(Databeat::getShardAudiobooks)
                .flatMap(Collection::stream)
                .collect(Collectors.toUnmodifiableList());
    }

    String consentHash() {
        return MessageDigester.sha256OfUTF8(allShardsAudiobooks().stream()
                .map(ShardAudiobook::getHashValue)
                .collect(Collectors.joining()));
    }

    Map<ShardName, List<ShardAudiobook>> allShardsAudiobooksByShardName() {
        return databeatMap.entrySet()
                .stream()
                .map(entry -> Map.entry(entry.getKey(), entry.getValue().getShardAudiobooks()))
                .collect(Collectors.toUnmodifiableMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    /**
     * Summarize sizes of all shard objects
     */
    Optional<Long> totalSizeOfAllObjects() {
        return allShardsAudiobooks()
                .stream()
                .map(ShardAudiobook::size)
                .reduce(Long::sum);
    }

    Set<ShardName> allShardNames() {
        return databeatMap.keySet();
    }

    @EventListener
    void onShardHighWatermark(final ShardHighWatermarkEvent event) {
        heartbeatHighWatermark.getAndSet(event.getHighWaterMark());
    }

    public int getHeartbeatHighWatermark() {
        return heartbeatHighWatermark.get();
    }

    /**
     * We can redistribute objects between shards if:
     * <ul>
     * <li>#HWM heartbeats == #databeats</li>
     * <li>#databeats > 1</li>
     * <li>#objects in shard > #shards (databeats)</li>
     * <li>all shards have same data</li>
     * </ul>
     */
    boolean canRedistribute() {
        final boolean numberOfHeartAndDatabeatsIsEqual = heartbeatHighWatermark.get() == numberOfDatabeats();
        final boolean moreThanOneDatabeatReceived = 2 <= numberOfDatabeats();
        final boolean moreObjectsThanShards = numberOfDatabeats() <= allShardsAudiobooks().size();
        return moreThanOneDatabeatReceived
                && numberOfHeartAndDatabeatsIsEqual
                && moreObjectsThanShards
                && isConsent();
    }

    /**
     * Do all shards agree with same distribution of all objects?
     * Every shards sends a hash value of its information of another shard's objects.
     * If every shard has computed same hash values -> that's a consent!
     * Because we all have the same data and using the same calculation will produce same result.
     */
    boolean isConsent() {
        final boolean hashValuesAreEqual;
        final Collection<Databeat> databeats = databeatMap.values();
        if (databeats.isEmpty()) {
            hashValuesAreEqual = false;
        } else {
            final List<String> hashValues = databeats.stream()
                    .map(Databeat::getConsentHash)
                    .collect(Collectors.toUnmodifiableList());
            hashValuesAreEqual = Collections.frequency(hashValues, hashValues.get(0)) == hashValues.size();
            if (hashValuesAreEqual) {
                LOGGER.info("All consent hash values ({}) are equal", hashValues);
            } else {
                LOGGER.warn("Consent hash values ({}) are not equal", hashValues);
            }
        }
        return hashValuesAreEqual;
    }

    @Override
    public String toString() {
        return String.format("DatabeatManager{%s}", databeatMap.size());
    }

}

package wbh.bookworm.hoerbuchdienst.adapter.required.daisy.audiobookrepository;

import javax.inject.Named;
import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import wbh.bookworm.hoerbuchdienst.domain.required.audiobookrepository.Databeats;
import wbh.bookworm.hoerbuchdienst.domain.required.audiobookrepository.ShardAudiobook;
import wbh.bookworm.hoerbuchdienst.domain.required.audiobookrepository.ShardName;

@Singleton
@Named("leastUsedShardDistributionStrategy")
class LeastUsedShardDistributionStrategyImpl implements ShardDistributionStrategy {

    private static final Logger LOGGER = LoggerFactory.getLogger(LeastUsedShardDistributionStrategyImpl.class);

    private static final double T24 = 1024.0d;

    @Override
    public List<ShardAudiobook> calculate(final int highWatermark, final Databeats databeats) {
        final Optional<Long> maybeTotalSize = databeats.totalSizeOfAllObjects();
        final List<ShardAudiobook> shardAudiobooks = new LinkedList<>(databeats.allShardAudiobooks());
        if (maybeTotalSize.isPresent()) {
            final double totalSizeOfAllObjects = maybeTotalSize.get();
            final double idealBytesPerShard = totalSizeOfAllObjects / (double) databeats.numberOfDatabeats();
            LOGGER.info("Total size of {} objects is {} = {} MB = {} GB," +
                            " ideal size of audiobooks on every shard: {} bytes = {} MB = {} GB",
                    databeats.numerOfObjects(),
                    totalSizeOfAllObjects, totalSizeOfAllObjects / T24 / T24, totalSizeOfAllObjects / T24 / T24 / T24,
                    idealBytesPerShard, idealBytesPerShard / T24 / T24, idealBytesPerShard / T24 / T24 / T24);
            // calculate new distribution across all shards
            // available shards
            final List<ShardName> availShards = List.copyOf(databeats.allShardNames());
            final Map<ShardName, AtomicLong> plannedBytesPerShard = new HashMap<>(availShards.size());
            for (final ShardName shardName : availShards) {
                plannedBytesPerShard.put(shardName, new AtomicLong(0L));
            }
            // sort audiobooks per hash value, so every shards has the same assumption
            shardAudiobooks.sort(Comparator.comparing(ShardAudiobook::getHashValue));
            // calculate distribution using audiobook's sizes
            final List<ShardAudiobook> distrib = new ArrayList<>(shardAudiobooks.size());
            for (final ShardAudiobook shardAudiobook : shardAudiobooks) {
                final ShardName currentShard = plannedBytesPerShard.entrySet()
                        .stream()
                        .min(Comparator.comparingLong(x -> x.getValue().get()))
                        .map(Map.Entry::getKey)
                        .orElseThrow();
                final ShardAudiobook placedAudiobook = ShardAudiobook.of(shardAudiobook, currentShard);
                plannedBytesPerShard.get(currentShard).addAndGet(placedAudiobook.size());
                distrib.add(placedAudiobook);
            }
            LOGGER.debug("{}", plannedBytesPerShard);
            LOGGER.debug("{}", distrib);
            return distrib;
        } else {
            LOGGER.warn("Gesamtgröße aller Objekte nicht vorhanden");
            return Collections.emptyList();
        }
    }

}

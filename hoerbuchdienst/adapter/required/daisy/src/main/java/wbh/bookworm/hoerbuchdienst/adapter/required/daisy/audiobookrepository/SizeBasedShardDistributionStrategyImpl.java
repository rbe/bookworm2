package wbh.bookworm.hoerbuchdienst.adapter.required.daisy.audiobookrepository;

import javax.inject.Named;
import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import wbh.bookworm.hoerbuchdienst.domain.required.audiobookrepository.Databeats;
import wbh.bookworm.hoerbuchdienst.domain.required.audiobookrepository.ShardAudiobook;
import wbh.bookworm.hoerbuchdienst.domain.required.audiobookrepository.ShardName;

@Singleton
@Named("sizeBasedShardDistributionStrategy")
class SizeBasedShardDistributionStrategyImpl implements ShardDistributionStrategy {

    private static final Logger LOGGER = LoggerFactory.getLogger(SizeBasedShardDistributionStrategyImpl.class);

    @Override
    public List<ShardAudiobook> calculate(final Databeats databeats) {
        final Optional<Long> maybeTotalSize = databeats.totalSize();
        final List<ShardAudiobook> shardAudiobooks = new LinkedList<>(databeats.allShardAudiobooks());
        if (maybeTotalSize.isPresent()) {
            final double totalSize = maybeTotalSize.get();
            final double bytesPerShard = totalSize / (double) databeats.count();
            LOGGER.info("Ideal size of audiobooks on every shard: {} bytes = {} MB = {} GB",
                    bytesPerShard, bytesPerShard / 1024.0d / 1024.0d, bytesPerShard / 1024.0d / 1024.0d / 1024.0d);
            // calculate new distribution across all shards
            shardAudiobooks.sort(Comparator.comparing(ShardAudiobook::getHashValue));
            final ShardName[] shardNames = databeats.allShardNames().toArray(ShardName[]::new);
            final List<ShardAudiobook> distrib = new ArrayList<>(shardAudiobooks.size());
            for (int i = 0, shardObjectsSize = shardAudiobooks.size(), numShards = shardNames.length; i < shardObjectsSize; i++) {
                distrib.add(ShardAudiobook.of(shardAudiobooks.get(i), shardNames[i % numShards]));
            }
            return distrib;
        } else {
            LOGGER.warn("Gesamtgröße aller Objekte nicht vorhanden");
            return Collections.emptyList();
        }
    }

}

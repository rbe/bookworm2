package wbh.bookworm.hoerbuchdienst.adapter.required.daisy.audiobookrepository;

import javax.inject.Named;
import javax.inject.Singleton;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import wbh.bookworm.hoerbuchdienst.domain.required.audiobookrepository.DataHeartbeats;
import wbh.bookworm.hoerbuchdienst.domain.required.audiobookrepository.ShardAudiobook;

@Singleton
@Named("sizeBasedShardDistributionStrategy")
class SizeBasedShardDistributionStrategyImpl implements ShardDistributionStrategy {

    private static final Logger LOGGER = LoggerFactory.getLogger(SizeBasedShardDistributionStrategyImpl.class);

    @Override
    public List<ShardAudiobook> calculate(final DataHeartbeats dataHeartbeats) {
        // TODO nur berechnen, wenn #heartbeats == #dataheartbeats
        final Optional<Long> maybeTotalSize = dataHeartbeats.totalSize();
        // TODO Berechnung nur notwendig, wenn #objekte > #shards
        final List<ShardAudiobook> shardObjects = new LinkedList<>(dataHeartbeats.allShardAudiobooks());
        if (maybeTotalSize.isPresent()) {
            final double totalSize = maybeTotalSize.get();
            final double bytesPerShard = totalSize / (double) dataHeartbeats.count();
            LOGGER.info("{} bytes = {} MB = {} GB", bytesPerShard,
                    bytesPerShard / 1024 / 1024,
                    bytesPerShard / 1024 / 1024 / 1024);
            shardObjects.sort(Comparator.comparing(ShardAudiobook::hashValue));
            // calculate new distribution across all shards
        } else {
            LOGGER.warn("Gesamtgröße aller Objekte nicht vorhanden");
        }
        return shardObjects;
    }

}

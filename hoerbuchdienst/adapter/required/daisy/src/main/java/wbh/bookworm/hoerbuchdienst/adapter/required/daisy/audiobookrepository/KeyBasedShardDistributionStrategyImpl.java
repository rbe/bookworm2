package wbh.bookworm.hoerbuchdienst.adapter.required.daisy.audiobookrepository;

import javax.inject.Named;
import javax.inject.Singleton;
import java.util.List;
import java.util.stream.Collectors;

import wbh.bookworm.hoerbuchdienst.domain.required.audiobookrepository.Databeats;
import wbh.bookworm.hoerbuchdienst.domain.required.audiobookrepository.ShardAudiobook;
import wbh.bookworm.hoerbuchdienst.domain.required.audiobookrepository.ShardName;
import wbh.bookworm.shared.domain.hoerbuch.Titelnummer;

@Singleton
@Named("keyBasedShardDistributionStrategy")
class KeyBasedShardDistributionStrategyImpl implements ShardDistributionStrategy {

    private static ShardName calculate(final ShardAudiobook shardAudiobook, final Databeats databeats) {
        final Titelnummer titelnummer = new Titelnummer(shardAudiobook.getObjectId());
        final int hashCode = MyHashCodeImpl.hashCode(titelnummer.getBytesUTF8());
        final int shardIndex = hashCode % databeats.numberOfDatabeats();
        return shardAudiobook.getShardName(); // TODO ShardName.of(shardIndex + 1);
    }

    @Override
    public List<ShardAudiobook> calculate(final int highWatermark, final Databeats databeats) {
        final List<ShardAudiobook> shardAudiobooks = databeats.allShardAudiobooks();
        return shardAudiobooks
                .stream()
                .map(shardAudiobook -> ShardAudiobook.of(shardAudiobook, calculate(shardAudiobook, databeats)))
                .collect(Collectors.toUnmodifiableList());
    }

}

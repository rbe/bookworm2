package wbh.bookworm.hoerbuchdienst.adapter.required.daisy.audiobookrepository;

import javax.inject.Named;
import javax.inject.Singleton;
import java.util.List;
import java.util.stream.Collectors;

import wbh.bookworm.hoerbuchdienst.domain.required.audiobookrepository.ShardAudiobook;
import wbh.bookworm.hoerbuchdienst.domain.required.audiobookrepository.ShardName;
import wbh.bookworm.shared.domain.hoerbuch.Titelnummer;

@Singleton
@Named("keyBasedShardDistributionStrategy")
class KeyBasedShardDistributionStrategyImpl implements ShardDistributionStrategy {

    private static ShardName calculate(final ShardAudiobook shardAudiobook, final DatabeatManager databeatManager) {
        final Titelnummer titelnummer = new Titelnummer(shardAudiobook.getObjectId());
        final int hashCode = -1; //MyHashValue.hashValue(titelnummer.getBytesUTF8());
        final int shardIndex = hashCode % databeatManager.numberOfDatabeats();
        return shardAudiobook.getShardName(); // TODO ShardName.of(shardIndex + 1);
    }

    @Override
    public List<ShardAudiobook> calculate(final int highWatermark, final DatabeatManager databeatManager) {
        final List<ShardAudiobook> shardAudiobooks = databeatManager.allShardsAudiobooks();
        return shardAudiobooks
                .stream()
                .map(shardAudiobook -> ShardAudiobook.of(shardAudiobook, calculate(shardAudiobook, databeatManager)))
                .collect(Collectors.toUnmodifiableList());
    }

}

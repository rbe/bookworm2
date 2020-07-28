package wbh.bookworm.hoerbuchdienst.adapter.required.daisy.audiobookrepository;

import javax.inject.Singleton;
import java.util.List;
import java.util.stream.Collectors;

import wbh.bookworm.hoerbuchdienst.domain.required.audiobookrepository.DataHeartbeats;
import wbh.bookworm.hoerbuchdienst.domain.required.audiobookrepository.ShardName;
import wbh.bookworm.hoerbuchdienst.domain.required.audiobookrepository.ShardObject;
import wbh.bookworm.shared.domain.hoerbuch.Titelnummer;

@Singleton
class KeyBasedShardDistributionStrategyImpl implements ShardDistributionStrategy {

    private static ShardName calculate(final ShardObject shardObject, final DataHeartbeats dataHeartbeats) {
        final Titelnummer titelnummer = new Titelnummer(shardObject.getObjectId());
        final int hashCode = MyHashCodeImpl.hashCode(titelnummer.getBytesUTF8());
        final int shardIndex = hashCode % dataHeartbeats.count();
        return shardObject.getShardName(); // TODO ShardName.of(shardIndex + 1);
    }

    @Override
    public List<ShardObject> calculate(final DataHeartbeats dataHeartbeats) {
        final List<ShardObject> shardObjects = dataHeartbeats.allShardObjects();
        return shardObjects
                .stream()
                .map(shardObject -> ShardObject.of(shardObject, calculate(shardObject, dataHeartbeats)))
                .collect(Collectors.toUnmodifiableList());
    }

}

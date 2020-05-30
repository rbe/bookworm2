package wbh.bookworm.hoerbuchdienst.adapter.required.daisy.audiobookrepository;

import javax.inject.Singleton;
import java.util.List;
import java.util.stream.Collectors;

import wbh.bookworm.hoerbuchdienst.domain.required.audiobookrepository.ShardNumber;
import wbh.bookworm.hoerbuchdienst.domain.required.audiobookrepository.ShardObject;
import wbh.bookworm.shared.domain.hoerbuch.Titelnummer;

@Singleton
class KeyBasedShardDistributionStrategyImpl implements ShardDistributionStrategy {

    private final HeartbeatMessageReceiver heartbeatMessageReceiver;

    KeyBasedShardDistributionStrategyImpl(final HeartbeatMessageReceiver heartbeatMessageReceiver) {
        this.heartbeatMessageReceiver = heartbeatMessageReceiver;
    }

    @Override
    public ShardNumber calculate(final ShardObject shardObject) {
        final Titelnummer titelnummer = new Titelnummer(shardObject.getTitelnummer());
        final int hashCode = MyHashCodeImpl.hashCode(titelnummer.getBytesUTF8());
        final int shardIndex = hashCode % heartbeatMessageReceiver.numberOfHeartbeats();
        return ShardNumber.of(shardIndex + 1);
    }

    @Override
    public List<ShardObject> calculate(final List<ShardObject> shardObjects) {
        return shardObjects
                .stream()
                .peek(shardObject -> shardObject.reshard(calculate(shardObject)))
                .collect(Collectors.toUnmodifiableList());
    }

}

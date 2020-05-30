package wbh.bookworm.hoerbuchdienst.adapter.required.daisy.audiobookrepository;

import java.util.List;

import wbh.bookworm.hoerbuchdienst.domain.required.audiobookrepository.ShardNumber;
import wbh.bookworm.hoerbuchdienst.domain.required.audiobookrepository.ShardObject;

class SizeBasedShardDistributionStrategyImpl implements ShardDistributionStrategy {

    @Override
    public ShardNumber calculate(final ShardObject shardObject) {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<ShardObject> calculate(final List<ShardObject> shardObjects) {
        // TODO check if distribution is equal (esp. size based)
        throw new UnsupportedOperationException();
    }

}

package wbh.bookworm.hoerbuchdienst.adapter.required.daisy.audiobookrepository;

import java.util.List;

import wbh.bookworm.hoerbuchdienst.domain.required.audiobookrepository.DataHeartbeats;
import wbh.bookworm.hoerbuchdienst.domain.required.audiobookrepository.ShardObject;

class SizeBasedShardDistributionStrategyImpl implements ShardDistributionStrategy {

    @Override
    public List<ShardObject> calculate(final DataHeartbeats dataHeartbeats) {
        // TODO check if distribution is equal (esp. size based)
        throw new UnsupportedOperationException();
    }

}

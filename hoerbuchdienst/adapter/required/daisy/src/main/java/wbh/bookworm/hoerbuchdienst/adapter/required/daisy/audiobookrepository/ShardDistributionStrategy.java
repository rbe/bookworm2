package wbh.bookworm.hoerbuchdienst.adapter.required.daisy.audiobookrepository;

import java.util.List;

import wbh.bookworm.hoerbuchdienst.domain.required.audiobookrepository.ShardNumber;
import wbh.bookworm.hoerbuchdienst.domain.required.audiobookrepository.ShardObject;

interface ShardDistributionStrategy {

    ShardNumber calculate(ShardObject shardObjects);

    List<ShardObject> calculate(List<ShardObject> shardObjects);

}

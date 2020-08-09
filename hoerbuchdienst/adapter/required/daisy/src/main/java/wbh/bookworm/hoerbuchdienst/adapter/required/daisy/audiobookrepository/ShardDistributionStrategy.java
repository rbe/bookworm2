package wbh.bookworm.hoerbuchdienst.adapter.required.daisy.audiobookrepository;

import java.util.List;

import wbh.bookworm.hoerbuchdienst.domain.required.audiobookrepository.ShardAudiobook;

@FunctionalInterface
interface ShardDistributionStrategy {

    List<ShardAudiobook> calculate(int highWatermark, DatabeatManager databeatManager);

}

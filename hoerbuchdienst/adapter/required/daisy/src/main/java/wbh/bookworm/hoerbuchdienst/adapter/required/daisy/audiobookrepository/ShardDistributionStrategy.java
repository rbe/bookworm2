package wbh.bookworm.hoerbuchdienst.adapter.required.daisy.audiobookrepository;

import java.util.List;

import wbh.bookworm.hoerbuchdienst.domain.required.audiobookrepository.Databeats;
import wbh.bookworm.hoerbuchdienst.domain.required.audiobookrepository.ShardAudiobook;

interface ShardDistributionStrategy {

    List<ShardAudiobook> calculate(Databeats databeats);

}

package wbh.bookworm.hoerbuchdienst.adapter.required.daisy.audiobookrepository;

import javax.inject.Singleton;
import java.util.List;

import io.micronaut.scheduling.annotation.Scheduled;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import wbh.bookworm.hoerbuchdienst.domain.required.audiobookrepository.AudiobookRepository;
import wbh.bookworm.hoerbuchdienst.domain.required.audiobookrepository.ShardingRepository;
import wbh.bookworm.shared.domain.hoerbuch.Titelnummer;

@Singleton
final class ScheduledReshardingInitiator {

    private static final Logger LOGGER = LoggerFactory.getLogger(ScheduledReshardingInitiator.class);

    private final DatabeatManager databeatManager;

    private final ShardingRepository shardingRepository;

    private final AudiobookRepository audiobookRepository;

    ScheduledReshardingInitiator(final DatabeatManager databeatManager,
                                 final ShardingRepository shardingRepository,
                                 final AudiobookRepository audiobookRepository) {
        this.databeatManager = databeatManager;
        this.shardingRepository = shardingRepository;
        this.audiobookRepository = audiobookRepository;
    }

    /* TODO 23-4 Uhr morgens in Produktion */
    @Scheduled(cron = "0 0/5 * * * *")
    void maybeRedistribute() {
        LOGGER.debug("");
        if (databeatManager.canRedistribute()) {
            LOGGER.info("Triggering shard redistribution");
            final List<Titelnummer> localDomainIds = audiobookRepository.allEntriesByKey();
            shardingRepository.redistribute(databeatManager.getHeartbeatHighWatermark(), localDomainIds);
        }
    }

}

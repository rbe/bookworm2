package wbh.bookworm.hoerbuchdienst.adapter.required.daisy.audiobookrepository;

import javax.inject.Inject;
import java.util.List;

import io.micronaut.configuration.rabbitmq.annotation.Queue;
import io.micronaut.configuration.rabbitmq.annotation.RabbitListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import wbh.bookworm.hoerbuchdienst.domain.required.audiobookrepository.AudiobookRepository;
import wbh.bookworm.hoerbuchdienst.domain.required.audiobookrepository.ShardObject;

@RabbitListener
class ReshardingMessageReceiver {

    private static final Logger LOGGER = LoggerFactory.getLogger(ReshardingMessageReceiver.class);

    private final AudiobookRepository audiobookRepository;

    @Inject
    ReshardingMessageReceiver(final AudiobookRepository audiobookRepository) {
        this.audiobookRepository = audiobookRepository;
    }

    @Queue(RepositoryQueues.HBD_RESHARD_LOCK)
    public void receiveLock(final boolean lock) {
        LOGGER.info("Received redistribute-lock: {}", lock);
        audiobookRepository.processShardRedistributionLock(lock);
    }

    @Queue(RepositoryQueues.HBD_RESHARD)
    public void receiveReshardObjects(final List<ShardObject> reshardObjects) {
        LOGGER.info("Received {} entries", reshardObjects.size());
        audiobookRepository.processRedistribution(reshardObjects);
    }

}

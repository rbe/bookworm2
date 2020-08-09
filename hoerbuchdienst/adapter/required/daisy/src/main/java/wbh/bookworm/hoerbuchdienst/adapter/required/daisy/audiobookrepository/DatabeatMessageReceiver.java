package wbh.bookworm.hoerbuchdienst.adapter.required.daisy.audiobookrepository;

import javax.inject.Singleton;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import io.micronaut.configuration.rabbitmq.annotation.Queue;
import io.micronaut.configuration.rabbitmq.annotation.RabbitListener;
import io.micronaut.messaging.annotation.Header;
import io.micronaut.runtime.event.annotation.EventListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import wbh.bookworm.hoerbuchdienst.domain.required.audiobookrepository.AudiobookRepository;
import wbh.bookworm.hoerbuchdienst.domain.required.audiobookrepository.Databeat;
import wbh.bookworm.hoerbuchdienst.domain.required.audiobookrepository.ShardAudiobook;
import wbh.bookworm.hoerbuchdienst.domain.required.audiobookrepository.ShardDisappearedEvent;
import wbh.bookworm.hoerbuchdienst.domain.required.audiobookrepository.ShardHighWatermarkEvent;
import wbh.bookworm.hoerbuchdienst.domain.required.audiobookrepository.ShardingRepository;
import wbh.bookworm.shared.domain.hoerbuch.Titelnummer;

@RabbitListener
@Singleton
final class DatabeatMessageReceiver {

    private static final Logger LOGGER = LoggerFactory.getLogger(DatabeatMessageReceiver.class);

    private final DatabeatManager databeatManager;

    private final ShardingRepository shardingRepository;

    private final AtomicInteger heartbeatHighWatermark;

    private final AudiobookRepository audiobookRepository;

    DatabeatMessageReceiver(final DatabeatManager databeatManager,
                            final ShardingRepository shardingRepository,
                            final AudiobookRepository audiobookRepository) {
        this.databeatManager = databeatManager;
        this.shardingRepository = shardingRepository;
        heartbeatHighWatermark = new AtomicInteger(0);
        this.audiobookRepository = audiobookRepository;
    }

    @Queue(RepositoryQueues.QUEUE_DATABEAT)
    void receiveDatabeat(@Header("x-shardname") final String xShardName, final Databeat databeat) {
        if (null != databeat.getShardAudiobooks()) {
            final List<ShardAudiobook> shardObjects = databeat.getShardAudiobooks();
            LOGGER.debug("Received {}", databeat);
            databeatManager.remember(databeat.getShardName(), databeat);
            LOGGER.info("Received {} entries from {}", shardObjects.size(), xShardName);
            if (databeatManager.canRedistribute(heartbeatHighWatermark.get())) {
                final List<Titelnummer> localDomainIds = audiobookRepository.allEntriesByKey();
                shardingRepository.redistribute(heartbeatHighWatermark.get(), localDomainIds);
            }
        } else {
            LOGGER.warn("List with shard objects from {} is empty", xShardName);
        }
    }

    @EventListener
    void onShardHighWatermark(final ShardHighWatermarkEvent event) {
        heartbeatHighWatermark.getAndSet(event.getHighWaterMark());
    }

    /**
     * When a shard disappears forget it and its objects.
     */
    @EventListener
    void onShardDisappeared(final ShardDisappearedEvent event) {
        LOGGER.debug("Removing {}'s data from cache as it disappeared", event.getShardName());
        databeatManager.forget(event.getShardName());
        LOGGER.info("Removed {}'s data from cache as it disappeared", event.getShardName());
    }

}

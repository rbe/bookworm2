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
import wbh.bookworm.hoerbuchdienst.domain.required.audiobookrepository.Databeats;
import wbh.bookworm.hoerbuchdienst.domain.required.audiobookrepository.ShardAudiobook;
import wbh.bookworm.hoerbuchdienst.domain.required.audiobookrepository.ShardDisappearedEvent;
import wbh.bookworm.hoerbuchdienst.domain.required.audiobookrepository.ShardHighWatermarkEvent;

@RabbitListener
@Singleton
final class DatabeatMessageReceiver {

    private static final Logger LOGGER = LoggerFactory.getLogger(DatabeatMessageReceiver.class);

    private final Databeats databeats;

    private final AudiobookRepository audiobookRepository;

    private final AtomicInteger highWatermark = new AtomicInteger(0);

    DatabeatMessageReceiver(final AudiobookRepository audiobookRepository) {
        this.audiobookRepository = audiobookRepository;
        databeats = new Databeats();
    }

    @Queue(RepositoryQueues.QUEUE_DATAHEARTBEAT)
    void receiveHeartbeat(@Header("x-shardname") final String xShardName, final Databeat databeat) {
        if (null != databeat.getShardAudiobooks()) {
            final List<ShardAudiobook> shardObjects = databeat.getShardAudiobooks();
            LOGGER.debug("Received {}", databeat);
            databeats.remember(databeat.getShardName(), databeat);
            LOGGER.info("Received {} entries from {}", shardObjects.size(), xShardName);
            // calculate only if:
            //     #databeats > 1
            //     #heartbeats == #databeats
            //     #objects > #databeats
            final boolean moreThanOneDatabeatReceived = 2 <= databeats.numberOfDatabeats();
            final boolean numberOfHeartAndDatabeatsIsEqual = highWatermark.get() == databeats.numberOfDatabeats();
            final boolean moreObjectsThanShards = databeats.numberOfDatabeats() <= databeats.allShardAudiobooks().size();
            if (moreThanOneDatabeatReceived && numberOfHeartAndDatabeatsIsEqual && moreObjectsThanShards) {
                audiobookRepository.maybeReshard(highWatermark, databeats);
            }
        } else {
            LOGGER.warn("List with shard objects from {} is empty", xShardName);
        }
    }

    @EventListener
    void onShardHighWaterMark(final ShardHighWatermarkEvent event) {
        highWatermark.getAndSet(event.getHighWaterMark());
    }

    /**
     * When a shard disappears forget it and its objects.
     */
    @EventListener
    void onShardDisappeared(final ShardDisappearedEvent event) {
        LOGGER.debug("Removing {}'s data from cache as it disappeared", event.getShardName());
        databeats.forget(event.getShardName());
        LOGGER.info("Removed {}'s data from cache as it disappeared", event.getShardName());
    }

}

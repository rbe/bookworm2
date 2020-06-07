package wbh.bookworm.hoerbuchdienst.adapter.required.daisy.audiobookrepository;

import javax.inject.Singleton;
import java.util.List;

import io.micronaut.configuration.rabbitmq.annotation.Queue;
import io.micronaut.configuration.rabbitmq.annotation.RabbitListener;
import io.micronaut.messaging.annotation.Header;
import io.micronaut.runtime.event.annotation.EventListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import wbh.bookworm.hoerbuchdienst.domain.required.audiobookrepository.AudiobookRepository;
import wbh.bookworm.hoerbuchdienst.domain.required.audiobookrepository.DataHeartbeat;
import wbh.bookworm.hoerbuchdienst.domain.required.audiobookrepository.DataHeartbeats;
import wbh.bookworm.hoerbuchdienst.domain.required.audiobookrepository.ShardDisappearedEvent;
import wbh.bookworm.hoerbuchdienst.domain.required.audiobookrepository.ShardObject;

@RabbitListener
@Singleton
class DataHeartbeatMessageReceiver {

    private static final Logger LOGGER = LoggerFactory.getLogger(DataHeartbeatMessageReceiver.class);

    private final DataHeartbeats dataHeartbeats;

    private final AudiobookRepository audiobookRepository;

    DataHeartbeatMessageReceiver(final AudiobookRepository audiobookRepository) {
        this.audiobookRepository = audiobookRepository;
        dataHeartbeats = new DataHeartbeats();
    }

    @Queue(RepositoryQueues.QUEUE_DATAHEARTBEAT)
    public void receiveHeartbeat(@Header("x-hostname") final String hostname, final DataHeartbeat dataHeartbeat) {
        final List<ShardObject> shardObjects = dataHeartbeat.getShardObjects();
        LOGGER.debug("Received {}", dataHeartbeat);
        dataHeartbeats.remember(hostname, dataHeartbeat);
        LOGGER.info("Received {} entries from {}", shardObjects.size(), hostname);
        audiobookRepository.maybeReshard(dataHeartbeats);
    }

    @EventListener
    public void onShardDisappeared(final ShardDisappearedEvent event) {
        LOGGER.debug("Shard disappeared, {}", event);
        // forget that shard and its objects
        dataHeartbeats.forget(event.getShardName());
        LOGGER.info("Removed {}'s data from cache as it disappeared", event.getShardName());
    }

}

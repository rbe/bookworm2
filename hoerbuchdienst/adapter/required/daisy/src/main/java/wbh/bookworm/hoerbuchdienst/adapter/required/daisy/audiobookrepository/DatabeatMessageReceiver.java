package wbh.bookworm.hoerbuchdienst.adapter.required.daisy.audiobookrepository;

import javax.inject.Singleton;
import java.util.List;

import io.micronaut.messaging.annotation.Header;
import io.micronaut.rabbitmq.annotation.Queue;
import io.micronaut.rabbitmq.annotation.RabbitListener;
import io.micronaut.runtime.event.annotation.EventListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import wbh.bookworm.hoerbuchdienst.domain.required.audiobookrepository.Databeat;
import wbh.bookworm.hoerbuchdienst.domain.required.audiobookrepository.ShardAudiobook;
import wbh.bookworm.hoerbuchdienst.domain.required.audiobookrepository.ShardDisappearedEvent;

@RabbitListener
@Singleton
final class DatabeatMessageReceiver {

    private static final Logger LOGGER = LoggerFactory.getLogger(DatabeatMessageReceiver.class);

    private final DatabeatManager databeatManager;

    DatabeatMessageReceiver(final DatabeatManager databeatManager) {
        this.databeatManager = databeatManager;
    }

    @Queue(ShardingQueues.QUEUE_DATABEAT)
    void receiveDatabeat(@Header("x-shardname") final String xShardName, final Databeat databeat) {
        if (null != databeat.getShardAudiobooks()) {
            final List<ShardAudiobook> shardObjects = databeat.getShardAudiobooks();
            LOGGER.debug("Received {}", databeat);
            databeatManager.remember(databeat.getShardName(), databeat);
            LOGGER.info("Received {} entries from {}", shardObjects.size(), xShardName);
        } else {
            LOGGER.warn("List with shard objects from {} is empty", xShardName);
        }
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

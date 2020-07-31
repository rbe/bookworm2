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
import wbh.bookworm.hoerbuchdienst.domain.required.audiobookrepository.Databeat;
import wbh.bookworm.hoerbuchdienst.domain.required.audiobookrepository.Databeats;
import wbh.bookworm.hoerbuchdienst.domain.required.audiobookrepository.ShardAudiobook;
import wbh.bookworm.hoerbuchdienst.domain.required.audiobookrepository.ShardDisappearedEvent;
import wbh.bookworm.hoerbuchdienst.domain.required.audiobookrepository.ShardName;

@RabbitListener
@Singleton
class DatabeatMessageReceiver {

    private static final Logger LOGGER = LoggerFactory.getLogger(DatabeatMessageReceiver.class);

    private final Databeats databeats;

    private final AudiobookRepository audiobookRepository;

    DatabeatMessageReceiver(final AudiobookRepository audiobookRepository) {
        this.audiobookRepository = audiobookRepository;
        databeats = new Databeats();
    }

    @Queue(RepositoryQueues.QUEUE_DATAHEARTBEAT)
    public void receiveHeartbeat(@Header("x-shardname") final String hostname, final Databeat databeat) {
        if (null != databeat.getShardAudiobooks()) {
            final List<ShardAudiobook> shardObjects = databeat.getShardAudiobooks();
            LOGGER.debug("Received {}", databeat);
            databeats.remember(databeat.getShardName(), databeat);
            LOGGER.info("Received {} entries from {}", shardObjects.size(), hostname);
            // TODO nur berechnen, wenn #heartbeats == #databeats
            // Berechnung nur notwendig, wenn mehr als 1 Shard vorhanden und #Objekte > #Shards
            final boolean moreThanOneDatabeatReceived = 2 <= databeats.count();
            final boolean moreObjectsThanShards = databeats.count() <= databeats.allShardAudiobooks().size();
            if (moreThanOneDatabeatReceived && moreObjectsThanShards) {
                audiobookRepository.maybeReshard(databeats);
            }
        } else {
            LOGGER.warn("List with shard objects from {} is empty", hostname);
        }
    }

    @EventListener
    public void onShardDisappeared(final ShardDisappearedEvent event) {
        LOGGER.debug("Shard disappeared, {}", event);
        // forget that shard and its objects
        databeats.forget(new ShardName(event.getShardName()));
        LOGGER.info("Removed {}'s data from cache as it disappeared", event.getShardName());
    }

}

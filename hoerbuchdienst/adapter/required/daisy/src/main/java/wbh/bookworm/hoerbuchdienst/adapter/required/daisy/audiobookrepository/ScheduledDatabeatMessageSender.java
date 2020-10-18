package wbh.bookworm.hoerbuchdienst.adapter.required.daisy.audiobookrepository;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Optional;

import io.micronaut.rabbitmq.exception.RabbitClientException;
import io.micronaut.scheduling.annotation.Scheduled;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import wbh.bookworm.hoerbuchdienst.domain.required.audiobookrepository.Databeat;
import wbh.bookworm.hoerbuchdienst.domain.required.audiobookrepository.ShardName;

@Singleton
class ScheduledDatabeatMessageSender {

    private static final Logger LOGGER = LoggerFactory.getLogger(ScheduledDatabeatMessageSender.class);

    private final ShardName shardName;

    private final DatabeatManager databeatManager;

    private final DatabeatMessageSender databeatMessageSender;

    @Inject
    ScheduledDatabeatMessageSender(final DatabeatManager databeatManager,
                                   final DatabeatMessageSender databeatMessageSender) {
        shardName = new ShardName();
        this.databeatManager = databeatManager;
        this.databeatMessageSender = databeatMessageSender;
    }

    @Scheduled(initialDelay = "20m", fixedDelay = "10m")
    void generateAndBroadcastDatabeat() {
        final Optional<Databeat> maybeDatabeat = databeatManager.getMyDatabeat();
        if (maybeDatabeat.isPresent()) {
            final Databeat databeat = maybeDatabeat.get();
            LOGGER.trace("Sending databeat {}", databeat);
            try {
                databeatMessageSender.send(shardName.toString(), databeat);
            } catch (RabbitClientException e) {
                LOGGER.error("Cannot send databeat", e);
            }
        } else {
            LOGGER.warn("Databeat not available");
        }
    }

}

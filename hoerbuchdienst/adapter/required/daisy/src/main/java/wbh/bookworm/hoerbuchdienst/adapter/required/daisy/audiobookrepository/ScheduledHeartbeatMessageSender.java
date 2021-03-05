package wbh.bookworm.hoerbuchdienst.adapter.required.daisy.audiobookrepository;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.time.ZonedDateTime;

import io.micronaut.rabbitmq.exception.RabbitClientException;
import io.micronaut.scheduling.annotation.Scheduled;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import wbh.bookworm.hoerbuchdienst.domain.required.audiobookrepository.Heartbeat;
import wbh.bookworm.hoerbuchdienst.domain.required.audiobookrepository.ShardName;

@Singleton
final class ScheduledHeartbeatMessageSender {

    private static final Logger LOGGER = LoggerFactory.getLogger(ScheduledHeartbeatMessageSender.class);

    private final ShardName shardName;

    private final HeartbeatMessageSender heartbeatMessageSender;

    @Inject
    ScheduledHeartbeatMessageSender(final HeartbeatMessageSender heartbeatMessageSender) {
        shardName = new ShardName();
        this.heartbeatMessageSender = heartbeatMessageSender;
    }

    @Scheduled(initialDelay = "30s", fixedDelay = "1s")
    void send() {
        final Heartbeat heartbeat = new Heartbeat(ZonedDateTime.now().toInstant(), shardName);
        LOGGER.trace("Sending heartbeat {}", heartbeat);
        try {
            heartbeatMessageSender.send(shardName.toString(), heartbeat);
        } catch (RabbitClientException e) {
            LOGGER.warn("{}", e.getMessage());
        }
    }

}

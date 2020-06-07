package wbh.bookworm.hoerbuchdienst.adapter.required.daisy.audiobookrepository;

import javax.inject.Singleton;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import io.micronaut.configuration.rabbitmq.annotation.Queue;
import io.micronaut.configuration.rabbitmq.annotation.RabbitListener;
import io.micronaut.context.event.ApplicationEventPublisher;
import io.micronaut.messaging.annotation.Header;
import io.micronaut.scheduling.annotation.Scheduled;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import wbh.bookworm.hoerbuchdienst.domain.required.audiobookrepository.Heartbeat;
import wbh.bookworm.hoerbuchdienst.domain.required.audiobookrepository.Heartbeats;
import wbh.bookworm.hoerbuchdienst.domain.required.audiobookrepository.ShardAppearedEvent;
import wbh.bookworm.hoerbuchdienst.domain.required.audiobookrepository.ShardDisappearedEvent;
import wbh.bookworm.hoerbuchdienst.domain.required.audiobookrepository.ShardReappearedEvent;

@RabbitListener
@Singleton
class HeartbeatMessageReceiver {

    private static final Logger LOGGER = LoggerFactory.getLogger(HeartbeatMessageReceiver.class);

    private final ApplicationEventPublisher eventPublisher;

    private final Heartbeats heartbeats;

    private final AtomicInteger numShardsHwm;

    HeartbeatMessageReceiver(final ApplicationEventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
        heartbeats = new Heartbeats();
        numShardsHwm = new AtomicInteger(0);
    }

    @Queue(RepositoryQueues.QUEUE_HEARTBEAT)
    public void receiveHeartbeat(@Header("x-hostname") final String hostname, final Heartbeat heartbeat) {
        LOGGER.trace("Received {} from {}", heartbeat, hostname);
        final boolean heartbeatNotInTime = heartbeat.getPointInTime().isBefore(Instant.now().minusSeconds(2L))
                || heartbeat.getPointInTime().isAfter(Instant.now().plusSeconds(2L));
        if (heartbeatNotInTime) {
            LOGGER.debug("Discarding heartbeat {} as its too old or in the future", heartbeat);
        } else {
            if (heartbeats.isLost(hostname)) {
                LOGGER.info("Welcome back, shard {}!", hostname);
                eventPublisher.publishEvent(new ShardReappearedEvent(hostname));
            }
            final boolean shardNew = null == heartbeats.remember(hostname, heartbeat);
            if (shardNew) {
                LOGGER.info("Welcome to our farm, shard {}!", hostname);
                eventPublisher.publishEvent(new ShardAppearedEvent(hostname));
            }
            maybeAddedShard();
        }
    }

    private void maybeAddedShard() {
        // increase high water mark?
        if (heartbeats.count() > numShardsHwm.get()) {
            // shard was added
            numShardsHwm.incrementAndGet();
            LOGGER.info("New high watermark: {} shards total", numShardsHwm);
        }
    }

    @Scheduled(fixedDelay = "5s")
    public void checkHeartbeats() {
        LOGGER.debug("Checking heartbeats");
        // check if a heartbeat is missing over some time
        final Instant heartbeatTooOld = Instant.now().minusSeconds(5L);
        final Map<String, Instant> lostHeartbeats = heartbeats.lastTimestamps()
                .entrySet()
                .stream()
                .filter(entry -> entry.getValue().isBefore(heartbeatTooOld))
                .collect(Collectors.toUnmodifiableMap(Map.Entry::getKey, Map.Entry::getValue));
        if (!lostHeartbeats.isEmpty()) {
            LOGGER.debug("Forgetting lost heartbeats {}", lostHeartbeats);
            heartbeats.forgetAll(lostHeartbeats.keySet());
            if (numShardsHwm.get() > heartbeats.count()) {
                LOGGER.error("Current number of heartbeats ({}) within 5 secs lower than high water mark ({})",
                        heartbeats.count(), numShardsHwm);
                lostHeartbeats.forEach((key, value) -> {
                    LOGGER.error("Shard {} disappeared!", key);
                    eventPublisher.publishEvent(new ShardDisappearedEvent(key));
                });
            }
        }
    }

}

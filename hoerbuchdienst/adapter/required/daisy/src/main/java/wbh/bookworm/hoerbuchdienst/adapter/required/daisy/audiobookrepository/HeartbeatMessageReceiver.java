package wbh.bookworm.hoerbuchdienst.adapter.required.daisy.audiobookrepository;

import javax.inject.Singleton;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import io.micronaut.context.event.ApplicationEventPublisher;
import io.micronaut.messaging.annotation.Header;
import io.micronaut.rabbitmq.annotation.Queue;
import io.micronaut.rabbitmq.annotation.RabbitListener;
import io.micronaut.scheduling.annotation.Scheduled;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import wbh.bookworm.hoerbuchdienst.domain.required.audiobookrepository.Heartbeat;
import wbh.bookworm.hoerbuchdienst.domain.required.audiobookrepository.ShardAppearedEvent;
import wbh.bookworm.hoerbuchdienst.domain.required.audiobookrepository.ShardDisappearedEvent;
import wbh.bookworm.hoerbuchdienst.domain.required.audiobookrepository.ShardHighWatermarkEvent;
import wbh.bookworm.hoerbuchdienst.domain.required.audiobookrepository.ShardName;
import wbh.bookworm.hoerbuchdienst.domain.required.audiobookrepository.ShardReappearedEvent;

@RabbitListener
@Singleton
final class HeartbeatMessageReceiver {

    private static final Logger LOGGER = LoggerFactory.getLogger(HeartbeatMessageReceiver.class);

    private static final long SECONDS = 30L;

    private final ShardName myShardName;

    private final HeartbeatManager heartbeatManager;

    private final AtomicInteger shardsHighWatermark;

    private final ApplicationEventPublisher eventPublisher;

    HeartbeatMessageReceiver(final HeartbeatManager heartbeatManager,
                             final ApplicationEventPublisher eventPublisher) {
        myShardName = new ShardName();
        this.heartbeatManager = heartbeatManager;
        this.eventPublisher = eventPublisher;
        shardsHighWatermark = new AtomicInteger(0);
    }

    // TODO Just receive and check message here, move logic to HearbeatManager
    @Queue(ShardingQueues.QUEUE_HEARTBEAT)
    void receiveHeartbeat(@Header("x-shardname") final String xShardName, final Heartbeat heartbeat) {
        LOGGER.trace("Received {} from {}", heartbeat, xShardName);
        final ShardName shardName = new ShardName(xShardName);
        final boolean heartbeatNotInTime = heartbeat.getPointInTime().isBefore(Instant.now().minusSeconds(2L))
                || heartbeat.getPointInTime().isAfter(Instant.now().plusSeconds(2L));
        if (heartbeatNotInTime) {
            LOGGER.warn("Discarding heartbeat {} as its timestamp is too old or in the future", heartbeat);
        } else {
            if (heartbeatManager.isLost(shardName)) {
                LOGGER.info("Welcome back, {}!", shardName);
                heartbeatManager.remember(shardName, heartbeat);
                eventPublisher.publishEvent(new ShardReappearedEvent(shardName));
            } else {
                final boolean shardIsNew = null == heartbeatManager.remember(shardName, heartbeat);
                if (shardIsNew) {
                    LOGGER.info("Welcome to our farm, {}!", shardName);
                    heartbeatManager.remember(shardName, heartbeat);
                    highWatermark();
                    eventPublisher.publishEvent(new ShardAppearedEvent(xShardName));
                }
            }
        }
    }

    private void highWatermark() {
        // increase high water mark?
        synchronized (shardsHighWatermark) { // TODO alle zugriffe müssen sync sein! -> In Hearbeats implementieren
            final boolean shardWasAdded = heartbeatManager.count() > shardsHighWatermark.get();
            if (shardWasAdded) {
                shardsHighWatermark.getAndSet(heartbeatManager.count());
                eventPublisher.publishEvent(new ShardHighWatermarkEvent(shardsHighWatermark.get()));
                LOGGER.info("New high watermark: {} shard(s) total", shardsHighWatermark);
            }
        }
    }

    @Scheduled(fixedDelay = SECONDS + "s")
    void checkHeartbeats() {
        LOGGER.trace("Checking other shard's heartbeats");
        // check if a heartbeat is missing over some time
        final Instant heartbeatTooOld = Instant.now().minusSeconds(SECONDS);
        final Map<ShardName, Instant> lostHeartbeats = heartbeatManager.lastTimestamps()
                .entrySet()
                .stream()
                .filter(entry -> entry.getValue().isBefore(heartbeatTooOld))
                .filter(entry -> !entry.getKey().equals(myShardName))
                .collect(Collectors.toUnmodifiableMap(Map.Entry::getKey, Map.Entry::getValue));
        if (!lostHeartbeats.isEmpty()) {
            LOGGER.warn("Forgetting lost heartbeats {}", lostHeartbeats);
            heartbeatManager.forgetAll(lostHeartbeats.keySet());
            if (shardsHighWatermark.get() > heartbeatManager.count()) {
                LOGGER.warn("Current number of heartbeats = {} within {} secs lower than high water mark = {}",
                        heartbeatManager.count(), SECONDS, shardsHighWatermark);
                lostHeartbeats.forEach((key, value) -> {
                    LOGGER.error("Shard {} disappeared!", key);
                    eventPublisher.publishEvent(new ShardDisappearedEvent(key));
                });
            }
        }
    }

}

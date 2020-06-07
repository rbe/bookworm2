package wbh.bookworm.hoerbuchdienst.adapter.required.daisy.audiobookrepository;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.time.ZonedDateTime;

import io.micronaut.context.annotation.Property;
import io.micronaut.scheduling.annotation.Scheduled;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import wbh.bookworm.hoerbuchdienst.domain.required.audiobookrepository.Heartbeat;
import wbh.bookworm.hoerbuchdienst.domain.required.audiobookrepository.ShardNumber;

@Singleton
final class ScheduledHeartbeatMessageSender {

    private static final Logger LOGGER = LoggerFactory.getLogger(ScheduledHeartbeatMessageSender.class);

    private final MyHostname myHostname;

    private final HeartbeatMessageSender heartbeatMessageSender;

    // TODO derive shard number from hostname (...shardN.audiobook.wbh-online.de)
    @Property(name = RepositoryConfigurationKeys.HOERBUCHDIENST_SHARD_NUMBER)
    private Integer myShardNumber;

    @Inject
    ScheduledHeartbeatMessageSender(final MyHostname myHostname,
                                    final HeartbeatMessageSender heartbeatMessageSender) {
        this.myHostname = myHostname;
        this.heartbeatMessageSender = heartbeatMessageSender;
    }

    @Scheduled(fixedDelay = "1s")
    void sendHeartbeat() {
        final Heartbeat heartbeat = new Heartbeat(ZonedDateTime.now().toInstant(), myHostname.myHostname(),
                ShardNumber.of(myShardNumber));
        LOGGER.trace("Sending heartbeat {}", heartbeat);
        heartbeatMessageSender.send(myHostname.myHostname(), heartbeat);
    }

}

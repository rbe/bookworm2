package wbh.bookworm.hoerbuchdienst.adapter.required.daisy.audiobookrepository;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.net.InetAddress;
import java.net.UnknownHostException;

import io.micronaut.scheduling.annotation.Scheduled;

import wbh.bookworm.hoerbuchdienst.adapter.required.daisy.streamresolver.AudiobookStreamResolver;

import aoc.mikrokosmos.objectstorage.api.ObjectMetaInfo;

@Singleton
final class ScheduledHeartbeatMessageSender {

    private static final long TOTAL_4TB = 4L * 1024L * 1024L * 1024L * 1024L;

    private final AudiobookStreamResolver audiobookStreamResolver;

    private final HeartbeatMessageSender heartbeatMessageSender;

    @Inject
    ScheduledHeartbeatMessageSender(final AudiobookStreamResolver audiobookStreamResolver,
                                    final HeartbeatMessageSender heartbeatMessageSender) {
        this.audiobookStreamResolver = audiobookStreamResolver;
        this.heartbeatMessageSender = heartbeatMessageSender;
    }

    @Scheduled(fixedDelay = "5s")
    void sendHeartbeat() {
        final Long usedBytes = audiobookStreamResolver.allObjectsMetaInfo()
                .stream()
                .map(ObjectMetaInfo::getLength)
                .reduce(0L, Long::sum);
        final String hostName;
        try {
            hostName = InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
        final HeartbeatInfo heartbeatInfo = new HeartbeatInfo(hostName, TOTAL_4TB, usedBytes);
        heartbeatMessageSender.heartbeat(hostName, heartbeatInfo);
    }

}

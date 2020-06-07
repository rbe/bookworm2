package wbh.bookworm.hoerbuchdienst.adapter.required.daisy.audiobookrepository;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.LongSummaryStatistics;
import java.util.Map;
import java.util.stream.Collectors;

import io.micronaut.context.annotation.Property;
import io.micronaut.scheduling.annotation.Scheduled;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import wbh.bookworm.hoerbuchdienst.adapter.required.daisy.streamresolver.AudiobookStreamResolver;
import wbh.bookworm.hoerbuchdienst.domain.required.audiobookrepository.DataHeartbeat;
import wbh.bookworm.hoerbuchdienst.domain.required.audiobookrepository.ShardNumber;
import wbh.bookworm.hoerbuchdienst.domain.required.audiobookrepository.ShardObject;
import wbh.bookworm.shared.domain.hoerbuch.Titelnummer;

import aoc.mikrokosmos.objectstorage.api.ObjectMetaInfo;

@Singleton
final class ScheduledDataHeartbeatMessageSender {

    private static final Logger LOGGER = LoggerFactory.getLogger(ScheduledDataHeartbeatMessageSender.class);

    private static final long TOTAL_4TB = 4L * 1024L * 1024L * 1024L * 1024L;

    private final MyHostname myHostname;

    private final AudiobookStreamResolver audiobookStreamResolver;

    private final DataHeartbeatMessageSender dataHeartbeatMessageSender;

    @Property(name = RepositoryConfigurationKeys.HOERBUCHDIENST_SHARD_NUMBER)
    private Integer myShardNumber;

    @Inject
    ScheduledDataHeartbeatMessageSender(final MyHostname myHostname,
                                        final AudiobookStreamResolver audiobookStreamResolver,
                                        final DataHeartbeatMessageSender dataHeartbeatMessageSender) {
        this.myHostname = myHostname;
        this.audiobookStreamResolver = audiobookStreamResolver;
        this.dataHeartbeatMessageSender = dataHeartbeatMessageSender;
    }

    @Scheduled(fixedDelay = "1m")
    void sendHeartbeat() {
        final List<ObjectMetaInfo> objectMetaInfos = audiobookStreamResolver.allObjectsMetaInfo();
        final Long usedBytes = objectMetaInfos.stream()
                .map(ObjectMetaInfo::getLength)
                .reduce(0L, Long::sum);
        final Map<Titelnummer, LongSummaryStatistics> statistics = objectMetaInfos.stream()
                /* TODO Mandantenspezifisch */.filter(objectMetaInfo -> objectMetaInfo.getObjectName().contains("Kapitel/"))
                .collect(Collectors.groupingBy(objectMetaInfo -> fromObjectName(objectMetaInfo.getObjectName()),
                        Collectors.summarizingLong(ObjectMetaInfo::getLength)));
        final List<ShardObject> shardObjects = statistics.entrySet()
                .stream()
                .map(entry -> Map.entry(entry.getKey(), entry.getValue().getSum()))
                .map(entry -> Map.entry(entry.getKey(), new ShardObject(entry.getKey().getValue(),
                        entry.getValue(), "", myShardNumber)))
                .map(Map.Entry::getValue)
                .collect(Collectors.toUnmodifiableList());
        final DataHeartbeat dataHeartbeat = new DataHeartbeat(ZonedDateTime.now().toInstant(), myHostname.myHostname(),
                TOTAL_4TB, usedBytes, ShardNumber.of(myShardNumber), shardObjects);
        LOGGER.trace("Sending data heartbeat {}", dataHeartbeat);
        dataHeartbeatMessageSender.send(myHostname.myHostname(), dataHeartbeat);
    }

    private Titelnummer fromObjectName(final String objectName) {
        try {
            final int idx = objectName.indexOf('/');
            return Titelnummer.of(objectName.substring(0, idx));
        } catch (/* TODO */IllegalArgumentException e) {
            return null;
        }
    }

    /*
    @Mapper
    public interface ObjectMetaInfoMapper {

        ObjectMetaInfoMapper INSTANCE = Mappers.getMapper(ObjectMetaInfoMapper.class);

        @Mapping(source = "objectMetaInfos.objectName", target = "id")
        @Mapping(source = "objectMetaInfos.length", target = "size")
        @Mapping(source = "shardNumber", target = "shardNumber")
        ShardObject convert(ObjectMetaInfo objectMetaInfos, ShardNumber shardNumber);

    }
    */

}

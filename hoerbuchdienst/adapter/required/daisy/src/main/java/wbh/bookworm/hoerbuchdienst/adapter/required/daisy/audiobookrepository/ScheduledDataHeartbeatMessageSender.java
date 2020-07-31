package wbh.bookworm.hoerbuchdienst.adapter.required.daisy.audiobookrepository;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import io.micronaut.configuration.rabbitmq.exception.RabbitClientException;
import io.micronaut.scheduling.annotation.Scheduled;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import wbh.bookworm.hoerbuchdienst.adapter.required.daisy.streamresolver.AudiobookStreamResolver;
import wbh.bookworm.hoerbuchdienst.domain.required.audiobookrepository.Databeat;
import wbh.bookworm.hoerbuchdienst.domain.required.audiobookrepository.ShardAudiobook;
import wbh.bookworm.hoerbuchdienst.domain.required.audiobookrepository.ShardName;
import wbh.bookworm.hoerbuchdienst.domain.required.audiobookrepository.ShardObject;
import wbh.bookworm.shared.domain.hoerbuch.Titelnummer;

import aoc.mikrokosmos.crypto.messagedigest.MessageDigester;
import aoc.mikrokosmos.objectstorage.api.ObjectMetaInfo;

@Singleton
final class ScheduledDataHeartbeatMessageSender {

    private static final Logger LOGGER = LoggerFactory.getLogger(ScheduledDataHeartbeatMessageSender.class);

    private static final long AVAILABLE_BYTES_4TB = (long) (4.5d * 1024.0 * 1024.0 * 1024.0 * 1024.0);

    private final ShardName shardName;

    private final AudiobookStreamResolver audiobookStreamResolver;

    private final DatabeatMessageSender databeatMessageSender;

    @Inject
    ScheduledDataHeartbeatMessageSender(final AudiobookStreamResolver audiobookStreamResolver,
                                        final DatabeatMessageSender databeatMessageSender) {
        this.shardName = new ShardName();
        this.audiobookStreamResolver = audiobookStreamResolver;
        this.databeatMessageSender = databeatMessageSender;
    }

    @Scheduled(fixedDelay = "1m")
    void sendHeartbeat() {
        final List<ObjectMetaInfo> objectMetaInfos = audiobookStreamResolver.allObjectsMetaInfo();
        final Long usedBytes = objectMetaInfos.stream()
                .map(ObjectMetaInfo::getLength)
                .reduce(0L, Long::sum);
        final Map<Titelnummer, List<ShardObject>> audiobookShardObjects = objectMetaInfos.stream()
                /* TODO Mandantenspezifisch */.filter(objectMetaInfo -> objectMetaInfo.getObjectName().contains("Kapitel/"))
                .map(omi -> new ShardObject(omi.getObjectName(), omi.getLength(), omi.getEtag()))
                .collect(Collectors.groupingBy(shardObject -> fromObjectName(shardObject.getObjectId()),
                        Collectors.toList()));
        final List<ShardAudiobook> shardAudiobooks = audiobookShardObjects.entrySet()
                .stream()
                .map(entry -> ShardAudiobook.local(entry.getKey().toString(), entry.getValue()))
                .collect(Collectors.toUnmodifiableList());
        final Databeat databeat = new Databeat(ZonedDateTime.now().toInstant(), shardName,
                AVAILABLE_BYTES_4TB, usedBytes, shardAudiobooks);
        LOGGER.trace("Sending databeat {}", databeat);
        try {
            databeatMessageSender.send(shardName.toString(), databeat);
        } catch (RabbitClientException e) {
            LOGGER.warn("{}", e.getMessage());
        }
    }

    private String audiobookHash(final Map<String, String> allShardObjectsHashes, final Titelnummer titelnummer) {
        final List<String> allEtags = allShardObjectsHashes.entrySet()
                .stream()
                .filter(entry -> entry.getKey().startsWith(titelnummer.getValue()))
                .map(Map.Entry::getValue)
                .collect(Collectors.toUnmodifiableList());
        return MessageDigester.ofUTF8(allEtags);
    }

    /**
     * Titelnummer aus Objektnamen ableiten
     */
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
        ShardObject convert(ObjectMetaInfo objectMetaInfos, ShardName shardNumber);

    }
    */

}

package wbh.bookworm.hoerbuchdienst.adapter.required.daisy.audiobookrepository;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.IOException;
import java.nio.file.FileStore;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import io.micronaut.configuration.rabbitmq.exception.RabbitClientException;
import io.micronaut.context.annotation.Value;
import io.micronaut.scheduling.annotation.Scheduled;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import wbh.bookworm.hoerbuchdienst.adapter.required.daisy.streamresolver.AudiobookStreamResolver;
import wbh.bookworm.hoerbuchdienst.domain.required.audiobookrepository.Databeat;
import wbh.bookworm.hoerbuchdienst.domain.required.audiobookrepository.ShardAudiobook;
import wbh.bookworm.hoerbuchdienst.domain.required.audiobookrepository.ShardName;
import wbh.bookworm.hoerbuchdienst.domain.required.audiobookrepository.ShardObject;
import wbh.bookworm.shared.domain.hoerbuch.Titelnummer;

import aoc.mikrokosmos.objectstorage.api.ObjectMetaInfo;

@Singleton
final class ScheduledDatabeatMessageSender {

    private static final Logger LOGGER = LoggerFactory.getLogger(ScheduledDatabeatMessageSender.class);

    private static final long SPACE_4GB = 4L * 1024L * 1024L * 1024L;

    private static final double T24 = 1024.0d;

    private final ShardName shardName;

    private final DatabeatManager databeatManager;

    private final AudiobookStreamResolver audiobookStreamResolver;

    private final DatabeatMessageSender databeatMessageSender;

    @Value("${hoerbuchdienst.objectstorage.path}")
    private Path objectStoragePath;

    @Inject
    ScheduledDatabeatMessageSender(final DatabeatManager databeatManager,
                                   final AudiobookStreamResolver audiobookStreamResolver,
                                   final DatabeatMessageSender databeatMessageSender) {
        this.shardName = new ShardName();
        this.databeatManager = databeatManager;
        this.audiobookStreamResolver = audiobookStreamResolver;
        this.databeatMessageSender = databeatMessageSender;
    }

    @Scheduled(fixedDelay = "1m")
    void send() {
        final List<ObjectMetaInfo> objectMetaInfos = audiobookStreamResolver.allObjectsMetaInfo();
        final Long usedBytes = objectMetaInfos.stream()
                .map(ObjectMetaInfo::getLength)
                .reduce(0L, Long::sum);
        final Map<Titelnummer, List<ShardObject>> audiobookShardObjects = objectMetaInfos.stream()
                /* TODO Mandantenspezifisch */.filter(objectMetaInfo -> objectMetaInfo.getObjectName().contains("Kapitel/"))
                .map(omi -> new ShardObject(omi.getObjectName(), omi.getLength(), omi.getEtag()))
                .collect(Collectors.groupingBy(shardObject -> fromObjectName(shardObject.getObjectId()),
                        Collectors.toList()));
        final List<ShardAudiobook> shardAudiobooks = audiobookShardObjects.entrySet().stream()
                .map(entry -> ShardAudiobook.local(entry.getKey().toString(), entry.getValue()))
                .collect(Collectors.toUnmodifiableList());
        long availableBytes;
        try {
            final FileStore fileStore = Files.getFileStore(objectStoragePath);
            availableBytes = fileStore.getTotalSpace() - SPACE_4GB;
            LOGGER.info("Filesystem {} type {} has {} available bytes = {} MB = {} GB",
                    fileStore.name(), fileStore.type(),
                    availableBytes, availableBytes / T24 / T24, availableBytes / T24 / T24 / T24);
        } catch (IOException e) {
            LOGGER.error("Cannot determine available space", e);
            availableBytes = -1L;
        }
        final Databeat databeat = new Databeat(ZonedDateTime.now().toInstant(), shardName,
                availableBytes, usedBytes, shardAudiobooks, databeatManager.consentHash());
        LOGGER.trace("Sending databeat {}", databeat);
        try {
            databeatMessageSender.send(shardName.toString(), databeat);
        } catch (RabbitClientException e) {
            LOGGER.warn("Cannot send databeat: {}", e.getMessage());
        }
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

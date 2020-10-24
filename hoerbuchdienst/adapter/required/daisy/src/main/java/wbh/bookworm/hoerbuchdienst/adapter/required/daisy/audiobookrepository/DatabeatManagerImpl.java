package wbh.bookworm.hoerbuchdienst.adapter.required.daisy.audiobookrepository;

import javax.annotation.PostConstruct;
import javax.inject.Singleton;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.FileStore;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.ZonedDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.micronaut.context.annotation.Value;
import io.micronaut.runtime.event.annotation.EventListener;
import io.micronaut.scheduling.annotation.Async;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import wbh.bookworm.hoerbuchdienst.adapter.required.daisy.streamresolver.AudiobookStreamResolver;
import wbh.bookworm.hoerbuchdienst.domain.required.audiobookrepository.Databeat;
import wbh.bookworm.hoerbuchdienst.domain.required.audiobookrepository.DatabeatManager;
import wbh.bookworm.hoerbuchdienst.domain.required.audiobookrepository.ShardAudiobook;
import wbh.bookworm.hoerbuchdienst.domain.required.audiobookrepository.ShardHighWatermarkEvent;
import wbh.bookworm.hoerbuchdienst.domain.required.audiobookrepository.ShardName;
import wbh.bookworm.hoerbuchdienst.domain.required.audiobookrepository.ShardObject;
import wbh.bookworm.shared.domain.Titelnummer;

import aoc.mikrokosmos.crypto.messagedigest.MessageDigester;
import aoc.mikrokosmos.objectstorage.api.BucketObjectRemovedEvent;
import aoc.mikrokosmos.objectstorage.api.ObjectMetaInfo;

@Singleton
class DatabeatManagerImpl implements DatabeatManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(DatabeatManagerImpl.class);

    private static final long SPACE_4GB = 4L * 1024L * 1024L * 1024L;

    private static final double T24 = 1024.0d;

    private final ShardName myShardName;

    private final Map<ShardName, Databeat> databeatMap;

    private final AtomicInteger heartbeatHighWatermark;

    private final ReentrantLock databeatGenerationLock;

    private final ObjectMapper objectMapper;

    private final Path databeatJson;

    private final Path objectStoragePath;

    private final AudiobookStreamResolver audiobookStreamResolver;

    DatabeatManagerImpl(@Value("${hoerbuchdienst.objectstorage.path}") final Path objectStoragePath,
                        final AudiobookStreamResolver audiobookStreamResolver) {
        myShardName = new ShardName();
        databeatMap = new ConcurrentHashMap<>(5);
        heartbeatHighWatermark = new AtomicInteger(0);
        databeatGenerationLock = new ReentrantLock();
        objectMapper = new ObjectMapper();
        databeatJson = Path.of("/var/local/databeat.json");
        this.objectStoragePath = objectStoragePath;
        this.audiobookStreamResolver = audiobookStreamResolver;
    }

    /**
     * Titelnummer aus Objektnamen ableiten
     */
    private static Titelnummer fromObjectName(final String objectName) {
        final int idx = objectName.indexOf((int) '/');
        final String titelnummer = objectName.substring(0, idx);
        try {
            return Titelnummer.of(titelnummer);
        } catch (IllegalArgumentException e) {
            throw new IllegalStateException(e);
        }
    }

    @PostConstruct
    private void postConstruct() {
        loadDatabeat();
        generate();
    }

    void loadDatabeat() {
        if (Files.exists(databeatJson)) {
            try (final InputStream inputStream = Files.newInputStream(databeatJson)) {
                final Databeat databeat = objectMapper.readValue(inputStream, Databeat.class);
                databeatMap.put(myShardName, databeat);
                LOGGER.info("Successfully loaded Databeat with {} entries", databeat.numberOfObjects());
            } catch (IOException e) {
                LOGGER.error(String.format("Could not load Databeat from %s", databeatJson), e);
            }
        } else {
            LOGGER.info("No persisted Databeat found at {}", databeatJson);
        }
    }

    @EventListener
    void processEvent(final BucketObjectRemovedEvent event) {
        if ("eingangskorb".equals(event.getBucketName())) {
            LOGGER.info("Generating Databeat as requested by {}", event);
            // TODO Just add event.getObjectName() to Databeat; what about other changes?
            generate();
        } else {
            LOGGER.debug("Ignoring event {}", event);
        }
    }

    @Override
    @Async
    public void generate() {
        try {
            if (databeatGenerationLock.tryLock(1L, TimeUnit.SECONDS)) {
                LOGGER.info("Generating Databeat");
                final long start = System.currentTimeMillis();
                final List<ObjectMetaInfo> objectMetaInfos = audiobookStreamResolver.objectsMetaInfo();
                final Long usedBytes = objectMetaInfos.stream()
                        .map(ObjectMetaInfo::getLength)
                        .reduce(0L, Long::sum);
                // Attention: use Etag value from MinIO as hash value
                final Map<Titelnummer, List<ShardObject>> audiobookShardObjects = objectMetaInfos.stream()
                        .peek(omi -> LOGGER.debug("omi: {}", omi))
                        .filter(objectMetaInfo -> objectMetaInfo.getObjectName().endsWith("DAISY/"))
                        .map(omi -> new ShardObject(omi.getObjectName(), omi.getLength(), omi.getEtag()))
                        .collect(Collectors.groupingBy(shardObject -> fromObjectName(shardObject.getObjectId()),
                                Collectors.toList()));
                final List<ShardAudiobook> shardAudiobooks = audiobookShardObjects.entrySet().stream()
                        .map(entry -> ShardAudiobook.local(entry.getKey().toString(), entry.getValue()))
                        .collect(Collectors.toUnmodifiableList());
                final long availableBytes = availableBytes();
                final long stop = System.currentTimeMillis();
                final long delta = stop - start;
                final Databeat myDatabeat = new Databeat(ZonedDateTime.now().toInstant(), myShardName,
                        availableBytes, usedBytes, shardAudiobooks, consentHash());
                LOGGER.info("Generating Databeat with {} entries took {} ms = {} s = {} min",
                        myDatabeat.numberOfObjects(), delta, delta / 1_000L, delta / 1_000L / 60L);
                databeatMap.put(myShardName, myDatabeat);
                persistDatabeat(myDatabeat);
                databeatGenerationLock.unlock();
            } else {
                LOGGER.warn("Could not acquire lock, generation already running");
            }
        } catch (InterruptedException e) {
            LOGGER.error("", e);
            Thread.currentThread().interrupt();
        }
    }

    void persistDatabeat(final Databeat myDatabeat) {
        try (final OutputStream outputStream = Files.newOutputStream(databeatJson)) {
            objectMapper.writeValue(outputStream, myDatabeat);
            LOGGER.info("Persisted Databeat with {} entries", myDatabeat.numberOfObjects());
        } catch (IOException e) {
            LOGGER.error("Cannot persist Databeat", e);
        }
    }

    private long availableBytes() {
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
        return availableBytes;
    }

    @Override
    public Optional<Databeat> getMyDatabeat() {
        return Optional.ofNullable(databeatMap.get(myShardName));
    }

    private String consentHash() {
        return MessageDigester.sha256OfUTF8(allShardsAudiobooks().stream()
                .map(ShardAudiobook::getHashValue)
                .sorted()
                .collect(Collectors.joining()));
    }

    @Override
    public void remember(final ShardName shardName, final Databeat databeat) {
        databeatMap.put(shardName, databeat);
    }

    @Override
    public void forget(final ShardName shardName) {
        databeatMap.remove(shardName);
    }

    boolean someReceived() {
        return !databeatMap.isEmpty();
    }

    /**
     * Number of received databeats
     */
    @Override
    public int numberOfDatabeats() {
        return databeatMap.size();
    }

    @Override
    public long numerOfObjects() {
        return (long) allShardsAudiobooks().size();
    }

    @Override
    public List<ShardAudiobook> allShardsAudiobooks() {
        return databeatMap.values()
                .stream()
                .map(Databeat::getShardAudiobooks)
                .flatMap(Collection::stream)
                .collect(Collectors.toUnmodifiableList());
    }

    @Override
    public Optional<ShardName> findShardNameForAudiobook(final String titelnummer) {
        return databeatMap.values()
                .stream()
                .map(Databeat::getShardAudiobooks)
                .flatMap(List::stream)
                .filter(entry -> entry.isTitelnummer(titelnummer))
                .map(ShardAudiobook::getShardName)
                .findFirst();
    }

    /**
     * Summarize sizes of all shard objects
     */
    @Override
    public Optional<Long> totalSizeOfAllObjects() {
        return allShardsAudiobooks()
                .stream()
                .map(ShardAudiobook::size)
                .reduce(Long::sum);
    }

    @Override
    public Set<ShardName> allShardNames() {
        return databeatMap.keySet();
    }

    @EventListener
    void onShardHighWatermark(final ShardHighWatermarkEvent event) {
        heartbeatHighWatermark.getAndSet(event.getHighWaterMark());
    }

    @Override
    public int getHeartbeatHighWatermark() {
        return heartbeatHighWatermark.get();
    }

    /**
     * We can redistribute objects between shards if:
     * <ul>
     * <li>#HWM heartbeats == #databeats</li>
     * <li>#databeats > 1</li>
     * <li>#objects in shard > #shards (databeats)</li>
     * <li>all shards have same data</li>
     * </ul>
     */
    @Override
    public boolean canRedistribute() {
        final boolean numberOfHeartAndDatabeatsIsEqual = heartbeatHighWatermark.get() == numberOfDatabeats();
        final boolean moreThanOneDatabeatReceived = 2 <= numberOfDatabeats();
        final boolean moreObjectsThanShards = numberOfDatabeats() <= allShardsAudiobooks().size();
        return moreThanOneDatabeatReceived
                && numberOfHeartAndDatabeatsIsEqual
                && moreObjectsThanShards
                && isConsent();
    }

    /**
     * Do all shards agree with same distribution of all objects?
     * Every shards sends a hash value of its information of another shard's objects.
     * If every shard has computed same hash values -> that's a consent!
     * Because we all have the same data and using the same calculation will produce same result.
     */
    @Override
    public boolean isConsent() {
        final boolean hashValuesAreEqual;
        final Collection<Databeat> databeats = databeatMap.values();
        if (databeats.isEmpty()) {
            hashValuesAreEqual = false;
        } else {
            final List<String> hashValues = databeats.stream()
                    .map(Databeat::getConsentHash)
                    .collect(Collectors.toUnmodifiableList());
            hashValuesAreEqual = Collections.frequency(hashValues, hashValues.get(0)) == hashValues.size();
            if (hashValuesAreEqual) {
                LOGGER.info("All consent hash values ({}) are equal", hashValues);
            } else {
                LOGGER.warn("Consent hash values ({}) are not equal", hashValues);
            }
        }
        return hashValuesAreEqual;
    }

    @Override
    public String toString() {
        return String.format("DatabeatManager{databeats=%d, heartbeatHwm=%d}",
                databeatMap.size(), heartbeatHighWatermark.get());
    }

}

/*
 * Copyright (C) 2011-2020 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchdienst.adapter.required.daisy.audiobookrepository;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import io.micronaut.cache.CacheManager;
import io.micronaut.context.event.ApplicationEventPublisher;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.MediaType;
import io.micronaut.http.MutableHttpRequest;
import io.micronaut.http.client.BlockingHttpClient;
import io.micronaut.http.client.HttpClient;
import io.micronaut.runtime.event.annotation.EventListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import wbh.bookworm.hoerbuchdienst.adapter.required.daisy.streamresolver.AudiobookStreamResolver;
import wbh.bookworm.hoerbuchdienst.domain.required.audiobookrepository.Audiobook;
import wbh.bookworm.hoerbuchdienst.domain.required.audiobookrepository.AudiobookShardingRepositoryException;
import wbh.bookworm.hoerbuchdienst.domain.required.audiobookrepository.ShardAudiobook;
import wbh.bookworm.hoerbuchdienst.domain.required.audiobookrepository.ShardDisappearedEvent;
import wbh.bookworm.hoerbuchdienst.domain.required.audiobookrepository.ShardName;
import wbh.bookworm.hoerbuchdienst.domain.required.audiobookrepository.ShardReappearedEvent;
import wbh.bookworm.hoerbuchdienst.domain.required.audiobookrepository.ShardStartServicingEvent;
import wbh.bookworm.hoerbuchdienst.domain.required.audiobookrepository.ShardStopServicingEvent;
import wbh.bookworm.hoerbuchdienst.domain.required.audiobookrepository.ShardingRepository;
import wbh.bookworm.shared.domain.hoerbuch.Titelnummer;

import aoc.mikrokosmos.crypto.messagedigest.FastByteHash;
import aoc.mikrokosmos.ddd.model.DomainId;

@Singleton
class AudiobookShardingRepositoryImpl implements ShardingRepository {

    private static final Logger LOGGER = LoggerFactory.getLogger(AudiobookShardingRepositoryImpl.class);

    private static final ShardName MY_SHARD_NAME = new ShardName();

    private static final int PORT_HTTPS = 443;

    private final DatabeatManager databeatManager;

    private final AudiobookStreamResolver audiobookStreamResolver;

    private final ShardDistributionStrategy shardDistributionStrategy;

    private final ApplicationEventPublisher eventPublisher;

    private final Lock redistributionLock;

    private final AtomicBoolean redistributionAllowed;

    private final CacheManager<Audiobook> cacheManager;

    private List<ShardAudiobook> allShardAudiobooks;

    @Inject
    AudiobookShardingRepositoryImpl(final DatabeatManager databeatManager,
                                    final AudiobookStreamResolver audiobookStreamResolver,
                                    @Named("leastUsedShardDistributionStrategy") final ShardDistributionStrategy shardDistributionStrategy,
                                    final ApplicationEventPublisher eventPublisher,
                                    final CacheManager<Audiobook> cacheManager) {
        this.databeatManager = databeatManager;
        this.audiobookStreamResolver = audiobookStreamResolver;
        this.shardDistributionStrategy = shardDistributionStrategy;
        this.eventPublisher = eventPublisher;
        this.cacheManager = cacheManager;
        allShardAudiobooks = Collections.emptyList();
        redistributionLock = new ReentrantLock();
        redistributionAllowed = new AtomicBoolean(true);
    }

    @Override
    public Optional<ShardName> lookupShard(/* TODO Mandantenspezifisch */final String titelnummer) {
        if (allShardAudiobooks.isEmpty()) {
            LOGGER.warn("Titelnummer {}: Keine Informationen über die Verteilung der Objekte auf Shards vorhanden",
                    titelnummer);
            return Optional.empty();
        } else {
            return allShardAudiobooks.stream()
                    .filter(shardAudiobook -> shardAudiobook.isTitelnummer(titelnummer))
                    .findFirst()
                    .map(ShardAudiobook::getShardName)
                    .or(Optional::empty);
        }
    }

    @EventListener
    void onShardDisappeared(final ShardDisappearedEvent event) {
        LOGGER.debug("Shard disappeared, {}", event);
        // TODO stop any active redistribution?
        // disallow redistribution
        final boolean witness = redistributionAllowed.compareAndExchange(Boolean.TRUE, Boolean.FALSE);
        if (witness) {
            // success, witness == expected value
            LOGGER.info("Successfully disallowed redistribution");
        } else {
            // failed, witness != expected value
            LOGGER.error("Cannot disallow redistribution");
        }
    }

    @EventListener
    void onShardReappeared(final ShardReappearedEvent event) {
        LOGGER.debug("Shard reappeared, {}", event);
        // allow redistribution again
        final boolean witness = redistributionAllowed.compareAndExchange(Boolean.FALSE, Boolean.TRUE);
        if (witness) {
            // failed, witness != expected value
            LOGGER.error("Cannot allow redistribution");
        } else {
            // success, witness == expected value
            LOGGER.info("Successfully allowed redistribution");
        }
    }

    private <T> T whileRedistributionAllowed(final String logIdent, final Supplier<? extends T> supplier, final Supplier<? extends T> empty) {
        if (redistributionAllowed.get()) {
            return supplier.get();
        } else {
            LOGGER.warn("{}: Currently not redistributing", logIdent);
            return empty.get();
        }
    }

    /**
     * Shard has received object list of all shards, calculates new distribution and
     * moves own objects not belonging here anymore to another shard.
     */
    @Override
    public void redistribute(final int heartbeatHighWatermark,
                             final List<? extends DomainId<String>> localDomainIds) {
        whileRedistributionAllowed("redistribute",
                () -> {
                    try {
                        // lock redistribution process
                        if (redistributionLock.tryLock(1L, TimeUnit.SECONDS)) {
                            final List<ShardAudiobook> desiredDistribution = shardDistributionStrategy.calculate(heartbeatHighWatermark, databeatManager);
                            allShardAudiobooks = desiredDistribution;
                            // check again redistribution requirements after calculation
                            if (databeatManager.canRedistribute()) {
                                // filter all objects in local object storage not belonging to this shard anymore
                                final List<ShardAudiobook> objectsToTransfer = desiredDistribution.stream()
                                        .filter(shardAudiobook -> localDomainIds.contains(new Titelnummer(shardAudiobook.getObjectId())))
                                        .filter(shardAudiobook -> !MY_SHARD_NAME.equals(shardAudiobook.getShardName()))
                                        .collect(Collectors.toUnmodifiableList());
                                if (!objectsToTransfer.isEmpty()) {
                                    // move all my objects now belonging to another shard
                                    objectsToTransfer.forEach(this::moveToOtherShard);
                                } else {
                                    LOGGER.debug("No objects to transfer to other shards");
                                }
                            } else {
                                LOGGER.warn("Redistribution requirements not met, consent: {}", databeatManager.isConsent());
                            }
                        } else {
                            LOGGER.warn("Redistribution currently running");
                        }
                    } catch (InterruptedException e) {
                        LOGGER.warn("Interrupted while trying to acquire redistribution lock");
                    }
                    return null;
                },
                () -> null);
    }

    @Override
    public boolean receiveObject(final String objectId, final InputStream inputStream, final long hashValue) {
        // TODO Titelnummer prüfen? final Titelnummer titelnummer = new Titelnummer(objectId);
        // TODO check if object was received and stored successfully (compare computed with received hash)
        final byte[] bytes;
        try {
            bytes = inputStream.readAllBytes();
        } catch (IOException e) {
            throw new AudiobookShardingRepositoryException("", e);
        }
        // compute hash value of received object/ZIP archive
        final long computedHashValue = FastByteHash.hash(bytes);
        audiobookStreamResolver.putZip(inputStream, objectId);
        final boolean equals = hashValue == computedHashValue;
        if (equals) {
            LOGGER.info("Hash value {} of received object {} equals computed hash value {}",
                    hashValue, objectId, computedHashValue);
        } else {
            LOGGER.error("Hash value {} of received object {} does not equal computed hash value {}",
                    hashValue, objectId, computedHashValue);
        }
        return equals;
    }

    private boolean moveToOtherShard(final ShardAudiobook shardAudiobook) {
        return whileRedistributionAllowed("moveToOtherShard",
                () -> {
                    boolean result = true;
                    final ShardName shardName = shardAudiobook.getShardName();
                    LOGGER.info("{} belongs to other shard {}", shardAudiobook, shardName);
                    URL baseUrl = null;
                    try {
                        baseUrl = new URL(String.format("https://%s:%d", shardName.getHostName(), PORT_HTTPS));
                    } catch (MalformedURLException e) {
                        LOGGER.error("Cannot build URL for shard {}", shardName);
                        result = false;
                    }
                    if (result) {
                        byte[] bytes = null;
                        final String objectId = shardAudiobook.getObjectId();
                        try (final InputStream zipAsStream = audiobookStreamResolver.zipAsStream(objectId)) {
                            bytes = zipAsStream.readAllBytes();
                        } catch (IOException e) {
                            LOGGER.error("Cannot retrieve object {} through StreamResolver", objectId);
                            result = false;
                        }
                        if (result) {
                            try (final HttpClient httpClient = HttpClient.create(baseUrl);
                                 final BlockingHttpClient blockingHttpClient = httpClient.toBlocking()) {
                                final long hashValue = FastByteHash.hash(bytes);
                                final String uri = String.format("/shard/redistribute/zip/%s/%s",
                                        objectId, hashValue);
                                LOGGER.debug("Sending {} with hash value {} to {}{}",
                                        objectId, hashValue, baseUrl, uri);
                                final MutableHttpRequest<byte[]> post = HttpRequest
                                        .POST(URI.create(uri), bytes)
                                        .contentType(MediaType.APPLICATION_OCTET_STREAM_TYPE)
                                        .accept(MediaType.APPLICATION_JSON_TYPE);
                                final String response = blockingHttpClient.retrieve(post);
                                LOGGER.info("Sent {} with hash value {} to {}/{}, response {}",
                                        objectId, hashValue, baseUrl, uri, response);
                                // stop servicing clients requests
                                eventPublisher.publishEvent(new ShardStopServicingEvent(MY_SHARD_NAME.getShardName()));
                                // TODO update location for moved audiobook
                                // invalidate cache
                                LOGGER.debug("Invalidating cache for object id {}", objectId);
                                try {
                                    cacheManager.getCache("audiobookRepository")
                                            .invalidate(shardAudiobook.getObjectId());
                                    LOGGER.info("Invalidated cache for object id {}", objectId);
                                } catch (Exception e) {
                                    LOGGER.error(String.format("Could not invalidate cache for object id %s", objectId), e);
                                }
                                // remove objects from object storage
                                audiobookStreamResolver.removeZip(objectId);
                                // start servicing clients requests again
                                eventPublisher.publishEvent(new ShardStartServicingEvent(MY_SHARD_NAME.getShardName()));
                            } catch (IOException e) {
                                LOGGER.error("", e);
                            }
                        } else {
                            LOGGER.warn("No data for audiobook {}", shardAudiobook);
                        }
                    } else {
                        LOGGER.warn("No shard URL for audiobook {}", shardAudiobook);
                    }
                    return result;
                },
                () -> false);
    }

}

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
import java.time.Duration;
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
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.MediaType;
import io.micronaut.http.MutableHttpRequest;
import io.micronaut.http.client.BlockingHttpClient;
import io.micronaut.http.client.DefaultHttpClient;
import io.micronaut.http.client.DefaultHttpClientConfiguration;
import io.micronaut.http.client.HttpClientConfiguration;
import io.micronaut.http.client.exceptions.ReadTimeoutException;
import io.micronaut.runtime.event.annotation.EventListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import wbh.bookworm.hoerbuchdienst.adapter.required.daisy.streamresolver.AudiobookStreamResolver;
import wbh.bookworm.hoerbuchdienst.domain.required.audiobookrepository.Audiobook;
import wbh.bookworm.hoerbuchdienst.domain.required.audiobookrepository.ShardAudiobook;
import wbh.bookworm.hoerbuchdienst.domain.required.audiobookrepository.ShardDisappearedEvent;
import wbh.bookworm.hoerbuchdienst.domain.required.audiobookrepository.ShardName;
import wbh.bookworm.hoerbuchdienst.domain.required.audiobookrepository.ShardReappearedEvent;
import wbh.bookworm.hoerbuchdienst.domain.required.audiobookrepository.ShardStartServicingEvent;
import wbh.bookworm.hoerbuchdienst.domain.required.audiobookrepository.ShardStopServicingEvent;
import wbh.bookworm.hoerbuchdienst.domain.required.audiobookrepository.ShardingRepository;
import wbh.bookworm.shared.domain.Titelnummer;

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
        redistributionLock = new ReentrantLock();
        redistributionAllowed = new AtomicBoolean(true);
    }

    @Override
    public Optional<ShardName> lookupShard(/* TODO Mandantenspezifisch */final String titelnummer) {
        return databeatManager.findShardNameForAudiobook(titelnummer);
    }

    @EventListener
    void onShardDisappeared(final ShardDisappearedEvent event) {
        LOGGER.debug("Shard {} disappeared, current state={}", event.getShardName(), redistributionAllowed.get());
        // TODO stop any active redistribution?
        // disallow redistribution
        disableRedistribution();
    }

    private void disableRedistribution() {
        final boolean witness = redistributionAllowed.compareAndExchange(Boolean.TRUE, Boolean.FALSE);
        if (witness) {
            // success, witness == expected value
            LOGGER.info("Successfully disallowed redistribution, witness={}", witness);
        } else {
            // failed, witness != expected value
            LOGGER.error("Redistribution already disallowed, witness={} expected=true", witness);
        }
    }

    @EventListener
    void onShardReappeared(final ShardReappearedEvent event) {
        LOGGER.debug("Shard {} reappeared, current state={}", event.getShardName(), redistributionAllowed.get());
        // allow redistribution again
        enableRedistribution();
    }

    private void enableRedistribution() {
        final boolean witness = redistributionAllowed.compareAndExchange(Boolean.FALSE, Boolean.TRUE);
        if (witness) {
            // failed, witness != expected value
            LOGGER.error("Redistribution already allowed, witness={}, expected=false", witness);
        } else {
            // success, witness == expected value
            LOGGER.info("Successfully allowed redistribution, witness={}", witness);
        }
    }

    private <T> T whileRedistributionAllowed(final String logIdent, final Supplier<? extends T> supplier, final Supplier<? extends T> empty) {
        T result = null;
        if (redistributionAllowed.get()) {
            disableRedistribution();
            try {
                result = supplier.get();
            } catch (Exception e) {
                LOGGER.error(String.format("%s", logIdent), e);
            } finally {
                enableRedistribution();
            }
        } else {
            LOGGER.warn("{}: Currently not redistributing", logIdent);
            result = empty.get();
        }
        LOGGER.debug("Redistribution action completed, result={}, redistribution allowed={}",
                result, redistributionAllowed.get());
        return result;
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
                            // check again redistribution requirements after calculation
                            if (databeatManager.canRedistribute()) {
                                findObjectsToMoveAndMoveThem(localDomainIds, desiredDistribution);
                            } else {
                                LOGGER.warn("Redistribution requirements not met, consent: {}", databeatManager.isConsent());
                            }
                        } else {
                            LOGGER.warn("Redistribution currently running (redistributionLock)");
                        }
                    } catch (InterruptedException e) {
                        LOGGER.warn("Interrupted while trying to acquire redistribution lock");
                    } finally {
                        try {
                            redistributionLock.unlock();
                        } catch (IllegalMonitorStateException e) {
                            LOGGER.error("", e);
                        }
                    }
                    return null;
                },
                () -> null);
    }

    private void findObjectsToMoveAndMoveThem(final List<? extends DomainId<String>> localDomainIds,
                                              final List<ShardAudiobook> desiredDistribution) {
        // filter all objects in local object storage not belonging to this shard anymore
        final List<ShardAudiobook> objectsToMove = desiredDistribution.stream()
                .filter(shardAudiobook -> localDomainIds.contains(new Titelnummer(shardAudiobook.getObjectId())))
                .filter(shardAudiobook -> !MY_SHARD_NAME.equals(shardAudiobook.getShardName()))
                .limit(2L)
                .collect(Collectors.toUnmodifiableList());
        if (objectsToMove.isEmpty()) {
            LOGGER.info("No objects to transfer to other shards");
        } else {
            // move all my objects now belonging to another shard
            objectsToMove.forEach(shardAudiobook -> {
                try {
                    moveToOtherShard(shardAudiobook);
                } catch (ReadTimeoutException e) {
                    LOGGER.error(String.format("Cannot move audiobook %s to %s",
                            shardAudiobook.getObjectId(), shardAudiobook.getShardName()), e);
                }
            });
        }
    }

    private boolean moveToOtherShard(final ShardAudiobook shardAudiobook) {
        boolean result = true;
        final ShardName otherShardName = shardAudiobook.getShardName();
        LOGGER.info("{} belongs to other shard {}", shardAudiobook, otherShardName);
        URL baseUrl = null;
        try {
            baseUrl = new URL(String.format("https://%s:%d", otherShardName.getShardName(), PORT_HTTPS));
        } catch (MalformedURLException e) {
            LOGGER.error("Cannot build URL for shard {}", otherShardName);
            result = false;
        }
        if (result) {
            byte[] bytes = null;
            final String objectId = shardAudiobook.getObjectId();
            try (final InputStream zipAsStream = audiobookStreamResolver.zipAsStream(objectId)) {
                bytes = /* TODO Bessere Möglichkeit für große Datenmengen? */zipAsStream.readAllBytes();
            } catch (IOException e) {
                LOGGER.error("Cannot retrieve object {} through StreamResolver", objectId);
                result = false;
            }
            if (result) {
                final HttpResponse<String> response = postWithHash(objectId, bytes, baseUrl);
                processMoveResponse(shardAudiobook, response);
            } else {
                LOGGER.warn("No data for audiobook {}", shardAudiobook);
            }
        } else {
            LOGGER.warn("No shard URL for audiobook {}", shardAudiobook);
        }
        return result;
    }

    @Override
    public void receiveObject(final String objectId, final InputStream inputStream) {
        // TODO Titelnummer prüfen? final Titelnummer titelnummer = new Titelnummer(objectId);
        audiobookStreamResolver.putZip(inputStream, objectId);
    }

    private void sendStartServicingRequestsEvent() {
        eventPublisher.publishEvent(new ShardStartServicingEvent(MY_SHARD_NAME.getShardName()));
    }

    private void sendStopServicingClientRequestsEvent() {
        eventPublisher.publishEvent(new ShardStopServicingEvent(MY_SHARD_NAME.getShardName()));
    }

    private HttpResponse<String> postWithHash(final String objectId, final byte[] bytes, final URL baseUrl) {
        HttpResponse<String> result = null;
        try (final BlockingHttpClient blockingHttpClient = getHttpClient(baseUrl)) {
            final long hashValue = FastByteHash.hash(bytes);
            final String uri = String.format("/shard/redistribute/zip/%s/%s", objectId, hashValue);
            LOGGER.debug("Sending {} with hash value {} to {}{}", objectId, hashValue, baseUrl, uri);
            final MutableHttpRequest<byte[]> post = HttpRequest
                    .POST(URI.create(uri), bytes)
                    .contentType(MediaType.APPLICATION_OCTET_STREAM_TYPE)
                    .accept(MediaType.APPLICATION_JSON_TYPE);
            final long startPost = System.currentTimeMillis();
            final HttpResponse<String> response = blockingHttpClient.exchange(post, String.class);
            final long stopPost = System.currentTimeMillis();
            LOGGER.info("Sent audiobook {} with hash value {} to {}/{}, response {}, took {} ms",
                    objectId, hashValue, baseUrl, uri, response, stopPost - startPost);
            result = response;
        } catch (IOException e) {
            LOGGER.error("", e);
        }
        return result;
    }

    private void processMoveResponse(final ShardAudiobook shardAudiobook,
                                     final HttpResponse<String> response) {
        if (HttpStatus.OK == response.getStatus()) {
            sendStopServicingClientRequestsEvent();
            invalidateCache(shardAudiobook);
            // location of moved audiobook is updated by next Databeat
            // TODO remove zip in a separate @Scheduled?
            audiobookStreamResolver.removeZip(shardAudiobook.getObjectId());
            LOGGER.info("Moved audiobook {} to {}", shardAudiobook.getObjectId(), shardAudiobook.getShardName());
            sendStartServicingRequestsEvent();
        } else {
            LOGGER.error("Could not move audiobook {} to {}", shardAudiobook.getObjectId(), shardAudiobook.getShardName());
        }
    }

    private void invalidateCache(final ShardAudiobook shardAudiobook) {
        final String objectId = shardAudiobook.getObjectId();
        LOGGER.debug("Invalidating cache for object id {}", objectId);
        try {
            cacheManager.getCache("audiobookRepository")
                    .invalidate(objectId);
            LOGGER.info("Invalidated cache for object id {}", objectId);
        } catch (Exception e) {
            LOGGER.error(String.format("Could not invalidate cache for object id %s", objectId), e);
        }
    }

    private BlockingHttpClient getHttpClient(final URL url) {
        final HttpClientConfiguration configuration = new DefaultHttpClientConfiguration();
        configuration.setReadTimeout(Duration.ofSeconds(30L));
        return new DefaultHttpClient(url, configuration).toBlocking();
    }

}

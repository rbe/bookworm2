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
import java.util.stream.Collectors;

import io.micronaut.http.HttpRequest;
import io.micronaut.http.MediaType;
import io.micronaut.http.MutableHttpRequest;
import io.micronaut.http.client.BlockingHttpClient;
import io.micronaut.http.client.HttpClient;
import io.micronaut.runtime.event.annotation.EventListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import wbh.bookworm.hoerbuchdienst.adapter.required.daisy.streamresolver.AudiobookStreamResolver;
import wbh.bookworm.hoerbuchdienst.domain.required.audiobookrepository.AudiobookShardingRepositoryException;
import wbh.bookworm.hoerbuchdienst.domain.required.audiobookrepository.ShardAudiobook;
import wbh.bookworm.hoerbuchdienst.domain.required.audiobookrepository.ShardDisappearedEvent;
import wbh.bookworm.hoerbuchdienst.domain.required.audiobookrepository.ShardName;
import wbh.bookworm.hoerbuchdienst.domain.required.audiobookrepository.ShardReappearedEvent;
import wbh.bookworm.hoerbuchdienst.domain.required.audiobookrepository.ShardingRepository;
import wbh.bookworm.shared.domain.hoerbuch.Titelnummer;

import aoc.mikrokosmos.crypto.messagedigest.FastByteHash;
import aoc.mikrokosmos.ddd.model.DomainId;

@Singleton
class AudiobookShardingRepositoryImpl implements ShardingRepository {

    public static final int PORT_HTTPS = 443;

    private static final Logger LOGGER = LoggerFactory.getLogger(AudiobookShardingRepositoryImpl.class);

    private final AudiobookStreamResolver audiobookStreamResolver;

    private final ShardDistributionStrategy shardDistributionStrategy;

    private final DatabeatManager databeatManager;

    private List<ShardAudiobook> allShardAudiobooks;

    @Inject
    AudiobookShardingRepositoryImpl(final DatabeatManager databeatManager,
                                    final AudiobookStreamResolver audiobookStreamResolver,
                                    @Named("leastUsedShardDistributionStrategy") final ShardDistributionStrategy shardDistributionStrategy) {
        this.databeatManager = databeatManager;
        this.audiobookStreamResolver = audiobookStreamResolver;
        this.shardDistributionStrategy = shardDistributionStrategy;
        allShardAudiobooks = Collections.emptyList();
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
    public void onShardDisappeared(final ShardDisappearedEvent event) {
        LOGGER.debug("Shard disappeared, {}", event);
        // TODO stop any active resharding
        // TODO disallow resharding
    }

    @EventListener
    public void onShardReappeared(final ShardReappearedEvent event) {
        LOGGER.debug("Shard reappeared, {}", event);
        // TODO allow resharding again
    }

    /**
     * Shard has received object list of all shards, calculates new distribution and
     * moves own objects not belonging here anymore to another shard.
     */
    @Override
    public void redistribute(final int heartbeatHighWatermark,
                             final List<? extends DomainId<String>> localDomainIds) {
        final List<ShardAudiobook> desiredDistribution = shardDistributionStrategy.calculate(heartbeatHighWatermark, databeatManager);
        allShardAudiobooks = desiredDistribution;
        // check again redistribution requirements after calculation
        if (databeatManager.canRedistribute(heartbeatHighWatermark)) {
            // filter all objects in local object storage not belonging to this shard anymore
            final ShardName myShardName = new ShardName();
            final List<ShardAudiobook> objectsToTransfer = desiredDistribution.stream()
                    .filter(shardAudiobook -> localDomainIds.contains(new Titelnummer(shardAudiobook.getObjectId())))
                    .filter(shardAudiobook -> !myShardName.equals(shardAudiobook.getShardName()))
                    .collect(Collectors.toUnmodifiableList());
            if (!objectsToTransfer.isEmpty()) {
                // move all my objects now belonging to another shard
                objectsToTransfer.forEach(this::moveToOtherShard);
                // TODO update locations for moved audiobook(s)
            }
        } else {
            LOGGER.warn("Redistribution requirements not met, consent: {}", databeatManager.isConsent());
        }
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
        boolean result = true;
        LOGGER.info("{} belongs to other shard {}", shardAudiobook, shardAudiobook.getShardName());
        URL baseUrl = null;
        try {
            baseUrl = new URL(String.format("https://%s:%d",
                    shardAudiobook.getShardName().getHostName(), PORT_HTTPS));
        } catch (MalformedURLException e) {
            LOGGER.error("Cannot build URL for shard {}", shardAudiobook.getShardName());
            result = false;
        }
        if (result) {
            byte[] bytes = null;
            try (final InputStream zipAsStream = audiobookStreamResolver
                    .zipAsStream(shardAudiobook.getObjectId())) {
                bytes = zipAsStream.readAllBytes();
            } catch (IOException e) {
                LOGGER.error("Cannot retrieve object {} through StreamResolver", shardAudiobook.getObjectId());
                result = false;
            }
            if (result) {
                try (final HttpClient httpClient = HttpClient.create(baseUrl);
                     final BlockingHttpClient blockingHttpClient = httpClient.toBlocking()) {
                    final long hashValue = FastByteHash.hash(bytes);
                    final String uri = String.format("/shard/redistribute/zip/%s/%s",
                            shardAudiobook.getObjectId(), hashValue);
                    LOGGER.debug("Sending {} with hash value {} to {}/{}",
                            shardAudiobook.getObjectId(), hashValue, baseUrl, uri);
                    final MutableHttpRequest<byte[]> post = HttpRequest
                            .POST(URI.create(uri), bytes)
                            .contentType(MediaType.APPLICATION_OCTET_STREAM_TYPE)
                            .accept(MediaType.APPLICATION_JSON_TYPE);
                    final String response = blockingHttpClient.retrieve(post);
                    LOGGER.info("Sent {} with hash value {} to {}/{}, response {}",
                            shardAudiobook.getObjectId(), hashValue, baseUrl, uri, response);
                    // TODO invalidate cache
                    // TODO remove objects from object storage
                    //audiobookStreamResolver.removeZip(objectId);
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
    }

}

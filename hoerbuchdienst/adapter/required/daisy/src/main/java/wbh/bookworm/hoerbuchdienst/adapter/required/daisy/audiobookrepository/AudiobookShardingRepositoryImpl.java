/*
 * Copyright (C) 2011-2020 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchdienst.adapter.required.daisy.audiobookrepository;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import io.micronaut.runtime.event.annotation.EventListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import wbh.bookworm.hoerbuchdienst.adapter.required.daisy.streamresolver.AudiobookStreamResolver;
import wbh.bookworm.hoerbuchdienst.domain.required.audiobookrepository.Databeats;
import wbh.bookworm.hoerbuchdienst.domain.required.audiobookrepository.ShardAudiobook;
import wbh.bookworm.hoerbuchdienst.domain.required.audiobookrepository.ShardDisappearedEvent;
import wbh.bookworm.hoerbuchdienst.domain.required.audiobookrepository.ShardName;
import wbh.bookworm.hoerbuchdienst.domain.required.audiobookrepository.ShardReappearedEvent;
import wbh.bookworm.hoerbuchdienst.domain.required.audiobookrepository.ShardingRepository;
import wbh.bookworm.shared.domain.hoerbuch.Titelnummer;

import aoc.mikrokosmos.ddd.model.DomainId;

@Singleton
class AudiobookShardingRepositoryImpl implements ShardingRepository {

    private static final Logger LOGGER = LoggerFactory.getLogger(AudiobookShardingRepositoryImpl.class);

    private final AudiobookStreamResolver audiobookStreamResolver;

    private final ShardDistributionStrategy shardDistributionStrategy;

    private List<ShardAudiobook> allShardAudiobooks;

    @Inject
    AudiobookShardingRepositoryImpl(final AudiobookStreamResolver audiobookStreamResolver,
                                    @Named("leastUsedShardDistributionStrategy") final ShardDistributionStrategy shardDistributionStrategy) {
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
    public void maybeReshard(final int highWatermark, final Databeats databeats,
                             final List<? extends DomainId<String>> localDomainIds) {
        final List<ShardAudiobook> desiredDistribution = shardDistributionStrategy
                .calculate(highWatermark, databeats);
        allShardAudiobooks = desiredDistribution;
        // filter all objects in local object storage not belonging to this shard (anymore)
        final ShardName myShardName = new ShardName();
        final List<ShardAudiobook> objectsToTransfer = desiredDistribution.stream()
                .filter(shardAudiobook -> localDomainIds.contains(new Titelnummer(shardAudiobook.getObjectId())))
                .filter(shardAudiobook -> !myShardName.equals(shardAudiobook.getShardName()))
                .collect(Collectors.toUnmodifiableList());
        // Move all my objects now belonging to another shard
        if (!objectsToTransfer.isEmpty()) {
            objectsToTransfer.forEach(shardAudiobook -> {
                LOGGER.info("{} belongs to other shard {}", shardAudiobook, shardAudiobook.getShardName());
                // TODO move object to another shard
                // TODO invalidate cache
                // TODO remove objects from object storage
            });
        }
    }

    @Override
    public boolean receiveObject(final String objectId, final InputStream inputStream) {
        // TODO Titelnummer prüfen? final Titelnummer titelnummer = new Titelnummer(objectId);
        audiobookStreamResolver.putZip(inputStream, objectId);
        return true;
    }

}

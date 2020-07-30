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
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import io.micronaut.cache.annotation.CacheConfig;
import io.micronaut.cache.annotation.Cacheable;
import io.micronaut.context.annotation.Property;
import io.micronaut.runtime.event.annotation.EventListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import wbh.bookworm.hoerbuchdienst.adapter.required.daisy.streamresolver.AudiobookStreamResolver;
import wbh.bookworm.hoerbuchdienst.domain.required.audiobookrepository.Audiobook;
import wbh.bookworm.hoerbuchdienst.domain.required.audiobookrepository.AudiobookMapper;
import wbh.bookworm.hoerbuchdienst.domain.required.audiobookrepository.AudiobookRepository;
import wbh.bookworm.hoerbuchdienst.domain.required.audiobookrepository.AudiobookRepositoryException;
import wbh.bookworm.hoerbuchdienst.domain.required.audiobookrepository.DataHeartbeats;
import wbh.bookworm.hoerbuchdienst.domain.required.audiobookrepository.ShardAudiobook;
import wbh.bookworm.hoerbuchdienst.domain.required.audiobookrepository.ShardDisappearedEvent;
import wbh.bookworm.hoerbuchdienst.domain.required.audiobookrepository.ShardName;
import wbh.bookworm.hoerbuchdienst.domain.required.audiobookrepository.ShardReappearedEvent;
import wbh.bookworm.shared.domain.hoerbuch.Titelnummer;

// TODO Event empfangen, um (gelöschtes/geändertes Hörbuch) aus dem Cache zu entfernen
@Singleton
@CacheConfig("audiobookRepository")
class AudiobookRepositoryImpl implements AudiobookRepository {

    private static final Logger LOGGER = LoggerFactory.getLogger(AudiobookRepositoryImpl.class);

    private final AudiobookStreamResolver audiobookStreamResolver;

    private final AudiobookMapper audiobookMapper;

    private final ShardDistributionStrategy shardDistributionStrategy;

    private List<ShardAudiobook> shardAudiobooks;

    @Property(name = RepositoryConfigurationKeys.HOERBUCHDIENST_TEMPORARY_PATH)
    private Path temporaryDirectory;

    @Inject
    AudiobookRepositoryImpl(final AudiobookStreamResolver audiobookStreamResolver,
                            final AudiobookMapper audiobookMapper,
                            @Named("sizeBasedShardDistributionStrategy") final ShardDistributionStrategy shardDistributionStrategy) {
        this.audiobookStreamResolver = audiobookStreamResolver;
        this.audiobookMapper = audiobookMapper;
        this.shardDistributionStrategy = shardDistributionStrategy;
    }

    @Override
    public Optional<ShardName> lookupShard(final String titelnummer) {
        if (shardAudiobooks.isEmpty()) {
            LOGGER.warn("Titelnummer {}: Keine Informationen über die Verteilung der Objekte auf Shards vorhanden",
                    titelnummer);
            return Optional.empty();
        } else {
            return shardAudiobooks.stream()
                    .filter(shardAudiobook -> shardAudiobook.hasTitelnummer(titelnummer))
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
     * shard receives object list (push, by message queue)
     */
    @Override
    public void maybeReshard(final DataHeartbeats dataHeartbeats) {
        shardAudiobooks = shardDistributionStrategy.calculate(dataHeartbeats);
        // filter all objects in local object storage not belonging to this shard (anymore)
        final ShardName itsme = new ShardName();
        final List<Titelnummer> myShardObjects = allEntriesByKey();
        final List<ShardAudiobook> foreignObjects = shardAudiobooks.stream()
                .filter(shardObject -> myShardObjects.contains(new Titelnummer(shardObject.getObjectId())))
                .filter(shardObject -> !itsme.equals(shardObject.getShardName()))
                .collect(Collectors.toUnmodifiableList());
        if (!foreignObjects.isEmpty()) {
            LOGGER.info("Moving {} to other shard(s)", foreignObjects);
            // TODO move all objects now belonging to another/recently added shard
            // TODO invalidate cache
            // TODO remove objects from object storage
        }
    }

    @Override
    public List<Titelnummer> allEntriesByKey() {
        return audiobookStreamResolver.listAll()
                .stream()
                // TODO "Kapitel" Suffix ist mandantenspezifisch
                .map(path -> new Titelnummer(path.getFileName().toString().replace("Kapitel", "")))
                .collect(Collectors.toUnmodifiableList());
    }

    @Override
    @Cacheable
    public Audiobook find(final String titelnummer) {
        final Audiobook audiobook = audiobookMapper.audiobook(titelnummer);
        if (null == audiobook) {
            throw new AudiobookRepositoryException(String.format("Hörbuch %s nicht gefunden", titelnummer));
        } else {
            return audiobook;
        }
    }

    @Override
    public Path makeLocalCopyOfTrack(final String hoerernummer,
                                     final String titelnummer, final String ident,
                                     final String temporaryId) {
        // TODO "Kapitel" Suffix ist mandantenspezifisch
        final String tempId = String.format("%sKapitel-%s-%s-%s", titelnummer, ident, UUID.randomUUID(), temporaryId);
        final Path tempMp3File = temporaryDirectory.resolve(hoerernummer).resolve(tempId);
        try {
            Files.createDirectories(tempMp3File.getParent());
        } catch (IOException e) {
            throw new AudiobookRepositoryException("", e);
        }
        try (final InputStream trackAsStream = trackAsStream(titelnummer, ident);
             final OutputStream tempMp3Stream = Files.newOutputStream(tempMp3File, StandardOpenOption.CREATE)) {
            trackAsStream.transferTo(tempMp3Stream);
            return tempMp3File;
        } catch (IOException e) {
            throw new AudiobookRepositoryException("", e);
        }
    }

    @Override
    public InputStream trackAsStream(final String titelnummer, final String ident) {
        return audiobookStreamResolver.trackAsStream(titelnummer, ident);
    }

    @Override
    public InputStream zipAsStream(final String titelnummer) {
        return audiobookStreamResolver.zipAsStream(titelnummer);
    }

    @Override
    public boolean putZip(final InputStream inputStream, final Titelnummer titelnummer) {
        audiobookStreamResolver.putZip(inputStream, titelnummer.getValue());
        return true;
    }

}

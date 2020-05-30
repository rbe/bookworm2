/*
 * Copyright (C) 2011-2020 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchdienst.adapter.required.daisy.audiobookrepository;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import io.micronaut.cache.annotation.CacheConfig;
import io.micronaut.cache.annotation.Cacheable;
import io.micronaut.context.annotation.Property;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import wbh.bookworm.hoerbuchdienst.adapter.required.daisy.streamresolver.AudiobookStreamResolver;
import wbh.bookworm.hoerbuchdienst.domain.required.audiobookrepository.Audiobook;
import wbh.bookworm.hoerbuchdienst.domain.required.audiobookrepository.AudiobookMapper;
import wbh.bookworm.hoerbuchdienst.domain.required.audiobookrepository.AudiobookRepository;
import wbh.bookworm.hoerbuchdienst.domain.required.audiobookrepository.AudiobookRepositoryException;
import wbh.bookworm.hoerbuchdienst.domain.required.audiobookrepository.ShardNumber;
import wbh.bookworm.hoerbuchdienst.domain.required.audiobookrepository.ShardObject;
import wbh.bookworm.shared.domain.hoerbuch.Titelnummer;

@Singleton
@CacheConfig("audiobookRepository")
class AudiobookRepositoryImpl implements AudiobookRepository {

    private static final Logger LOGGER = LoggerFactory.getLogger(AudiobookRepositoryImpl.class);

    @Property(name = RepositoryConfigurationKeys.HOERBUCHDIENST_SHARD_ID)
    private static final ShardNumber MY_SHARD_NUMBER = ShardNumber.of(1);

    private static final List<ShardObject> staticShardObjects = new ArrayList<>();

    static {
        for (int i = 1; i < 2_000 + 1; i++) {
            final String titelnummer = String.format("%05d", i);
            staticShardObjects.add(new ShardObject(ShardNumber.of(1), titelnummer, 380 * 1024L * 1024L, "" + titelnummer.hashCode()));
        }
    }

    private final ShardDistributionStrategy shardDistributionStrategy;

    private final ReshardingMessageSender reshardingMessageSender;

    @Property(name = RepositoryConfigurationKeys.HOERBUCHDIENST_TEMPORARY_PATH)
    private Path temporaryDirectory;

    private final AudiobookStreamResolver audiobookStreamResolver;

    private final AudiobookMapper audiobookMapper;

    private final Semaphore reshardingInProgressLock = new Semaphore(1);

    // TODO Event empfangen, um (gelöschtes/geändertes Hörbuch) aus dem Cache zu entfernen

    private final Object reshardLockMonitor = new Object();

    private List<ShardObject> shardObjects;

    @Inject
    AudiobookRepositoryImpl(final ShardDistributionStrategy shardDistributionStrategy,
                            final ReshardingMessageSender reshardingMessageSender,
                            final AudiobookStreamResolver audiobookStreamResolver,
                            final AudiobookMapper audiobookMapper) {
        this.shardDistributionStrategy = shardDistributionStrategy;
        this.reshardingMessageSender = reshardingMessageSender;
        shardObjects = new ArrayList<>();
        this.audiobookStreamResolver = audiobookStreamResolver;
        this.audiobookMapper = audiobookMapper;
    }

    @Override
    public ShardNumber lookupShard(final String titelnummer) {
        return shardObjects
                .stream()
                .filter(shardObject -> shardObject.getTitelnummer().equals(titelnummer))
                .findFirst()
                .get()
                .getShardNumber();
    }

    @Override
    public void processShardRedistributionLock(final boolean lock) {
        if (lock) {
            // set a flag that lock is already held in another shard
            try {
                if (reshardingInProgressLock.tryAcquire(100, TimeUnit.MILLISECONDS)) {
                    LOGGER.debug("Sucessfully acquired shardRedistributionLock");
                }
            } catch (InterruptedException e) {
                LOGGER.error("Cannot acquire shardRedistributionLock", e);
            }
        } else {
            // unset a flag that lock is already held in another shard
            maybeUnlockRedistributionRunning();
        }
    }

    @Override
    // TODO @Schedule, damit redistributing nicht lange Zeit in Anspruch nimmt?
    public void startResharding() {
        // set redistribution lock on all shards
        if (acquireShardRedistributionLock()) {
            reshardingMessageSender.lock(true);
            // TODO update shard distribution from all shards
            final List<ShardObject> updatedShardObjects = shardDistributionStrategy.calculate(staticShardObjects);
            reshardingMessageSender.send(updatedShardObjects);
        }
    }

    private boolean acquireShardRedistributionLock() {
        synchronized (reshardLockMonitor) {
            try {
                final boolean b = reshardingInProgressLock.tryAcquire(250L, TimeUnit.MILLISECONDS);
                if (b) {
                    LOGGER.debug("Sucessfully acquired shardRedistributionLock");
                } else {
                    LOGGER.warn("redistributionRunningLock already taken");
                }
                return b;
            } catch (InterruptedException e) {
                LOGGER.error("Acquiring shardRedistributionLock was interrupted", e);
                if (Thread.interrupted()) {
                    LOGGER.debug("Cleared thread interrupted status");
                }
            }
            return false;
        }
    }

    private void maybeUnlockRedistributionRunning() {
        synchronized (reshardLockMonitor) {
            if (0 == reshardingInProgressLock.availablePermits()) {
                try {
                    reshardingInProgressLock.release(1);
                    LOGGER.debug("Unlocked shardRedistributionLock");
                } catch (IllegalMonitorStateException e) {
                    // I did not held the lock
                    LOGGER.debug("Cannot unlock shardRedistributionLock", e);
                }
            } else {
                LOGGER.warn("Cannot unlock shardRedistributionLock, no lock was acquired before");
            }
        }
    }

    /**
     * shard receives object list (push, by message queue)
     */
    @Override
    public void processRedistribution(final List<ShardObject> shardObjects) {
        // filter all objects not belonging to this shard anymore
        final List<ShardObject> foreignObjects = shardObjects
                .stream()
                .filter(shardObject -> !MY_SHARD_NUMBER.equals(shardObject.getShardNumber()))
                .collect(Collectors.toUnmodifiableList());
        // move all objects now belonging to another/recently added shard
        LOGGER.info("Moving {} to other shards", foreignObjects);
        // shard receives object (push, by REST endpoint)
        // check if objects are received successfully (compare hash)
        // remove redistribution lock on all shards, TODO when all objects were redistributed
        reshardingMessageSender.lock(false);
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

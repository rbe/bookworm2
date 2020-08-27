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
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;
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
import wbh.bookworm.hoerbuchdienst.domain.required.audiobookrepository.ShardStartServicingEvent;
import wbh.bookworm.hoerbuchdienst.domain.required.audiobookrepository.ShardStopServicingEvent;
import wbh.bookworm.shared.domain.hoerbuch.Titelnummer;

// TODO Event empfangen, um (gelöschtes/geändertes Hörbuch) aus dem Cache zu entfernen
@Singleton
@CacheConfig("audiobookRepository")
class AudiobookRepositoryImpl implements AudiobookRepository {

    private static final Logger LOGGER = LoggerFactory.getLogger(AudiobookRepositoryImpl.class);

    private final AudiobookStreamResolver audiobookStreamResolver;

    private final AudiobookMapper audiobookMapper;

    private final AtomicBoolean servicingAudiobookRequests;

    @Property(name = RepositoryConfigurationKeys.HOERBUCHDIENST_TEMPORARY_PATH)
    private Path temporaryDirectory;

    @Inject
    AudiobookRepositoryImpl(final AudiobookStreamResolver audiobookStreamResolver,
                            final AudiobookMapper audiobookMapper) {
        this.audiobookStreamResolver = audiobookStreamResolver;
        this.audiobookMapper = audiobookMapper;
        servicingAudiobookRequests = new AtomicBoolean(true);
    }

    @EventListener
    void onStartServicing(final ShardStartServicingEvent event) {
        LOGGER.debug("Start servicing requests, current state={}", servicingAudiobookRequests.get());
        final boolean witness = servicingAudiobookRequests.compareAndExchange(Boolean.FALSE, Boolean.TRUE);
        if (witness) {
            // failed, witness != expected value
            LOGGER.warn("Servicing requests already started, witness={}, expected=false", witness);
        } else {
            // success, witness == expected value
            LOGGER.info("Successfully started servicing requests, witness={}", witness);
        }
    }

    @EventListener
    void onStopServicing(final ShardStopServicingEvent event) {
        LOGGER.info("Stop servicing requests, current state={}", servicingAudiobookRequests.get());
        final boolean witness = servicingAudiobookRequests.compareAndExchange(Boolean.TRUE, Boolean.FALSE);
        if (witness) {
            // success, witness == expected value
            LOGGER.info("Successfully stopped servicing requests, witness={}", witness);
        } else {
            // failed, witness != expected value
            LOGGER.warn("Servicing requests already stopped, witness={} expected=true", witness);
        }
    }

    private <T> T whileServicing(final String logIdent, final Supplier<? extends T> supplier, final Supplier<? extends T> empty) {
        if (servicingAudiobookRequests.get()) {
            return supplier.get();
        } else {
            LOGGER.warn("{}: Currently not servicing requests", logIdent);
            return empty.get();
        }
    }

    @Override
    public List<Titelnummer> allEntriesByKey() {
        return whileServicing("allEntriesByKey", () -> audiobookStreamResolver.listAll()
                        .stream()
                        .map(path -> new Titelnummer(path.getFileName().toString()
                                .replace("DAISY", "")))
                        .collect(Collectors.toUnmodifiableList()),
                Collections::emptyList);
    }

    @Override
    @Cacheable
    public Audiobook find(final String titelnummer) {
        return whileServicing("find",
                () -> {
                    final Audiobook audiobook = audiobookMapper.audiobook(titelnummer);
                    if (null == audiobook) {
                        throw new AudiobookRepositoryException(String.format("Hörbuch %s nicht gefunden", titelnummer));
                    } else {
                        return audiobook;
                    }
                },
                () -> Audiobook.UNKNOWN);
    }

    @Override
    public Path trackAsFile(final String hoerernummer,
                            final String titelnummer, final String ident,
                            final String temporaryId) {
        return whileServicing("trackAsFile",
                () -> {
                    final String tempId = String.format("%sDAISY-%s-%s-%s", titelnummer, ident, UUID.randomUUID(), temporaryId);
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
                },
                () -> null);
    }

    @Override
    public InputStream trackAsStream(final String titelnummer, final String ident) {
        return whileServicing("trackAsStream",
                () -> audiobookStreamResolver.trackAsStream(titelnummer, ident),
                () -> null);
    }

    @Override
    public InputStream zipAsStream(final String titelnummer) {
        return whileServicing("zipAsStream",
                () -> audiobookStreamResolver.zipAsStream(titelnummer),
                () -> null);
    }

}

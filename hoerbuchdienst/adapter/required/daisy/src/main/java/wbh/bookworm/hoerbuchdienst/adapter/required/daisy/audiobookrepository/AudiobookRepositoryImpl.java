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
import java.util.UUID;

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

@Singleton
@CacheConfig("audiobookRepository")
class AudiobookRepositoryImpl implements AudiobookRepository {

    private static final Logger LOGGER = LoggerFactory.getLogger(AudiobookRepositoryImpl.class);

    @Property(name = RepositoryConfigurationKeys.HOERBUCHDIENST_TEMPORARY_PATH)
    private Path temporaryDirectory;

    private final AudiobookStreamResolver audiobookStreamResolver;

    private final AudiobookMapper audiobookMapper;

    @Inject
    AudiobookRepositoryImpl(final AudiobookStreamResolver audiobookStreamResolver,
                            final AudiobookMapper audiobookMapper) {
        this.audiobookStreamResolver = audiobookStreamResolver;
        this.audiobookMapper = audiobookMapper;
    }

    // TODO Event empfangen, um (gelöschtes/geändertes Hörbuch) aus dem Cache zu entfernen

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
    public Path localCopyOfTrack(final String hoerernummer,
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

}

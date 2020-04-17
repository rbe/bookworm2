/*
 * Copyright (C) 2011-2020 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchdienst.adapter.required.daisyaudiobook;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

import com.mpatric.mp3agic.InvalidDataException;
import com.mpatric.mp3agic.Mp3File;
import com.mpatric.mp3agic.NotSupportedException;
import com.mpatric.mp3agic.UnsupportedTagException;
import io.micronaut.context.BeanContext;
import io.micronaut.context.annotation.Property;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import wbh.bookworm.hoerbuchdienst.domain.required.audiobook.Audiobook;
import wbh.bookworm.hoerbuchdienst.domain.required.audiobook.AudiobookMapper;
import wbh.bookworm.hoerbuchdienst.domain.required.audiobook.AudiobookRepository;
import wbh.bookworm.hoerbuchdienst.domain.required.audiobookindex.AudiobookIndex;

@Singleton
class AudiobookRepositoryImpl implements AudiobookRepository {

    private static final Logger LOGGER = LoggerFactory.getLogger(AudiobookRepositoryImpl.class);

    private static final byte[] EMPTY_BYTES = new byte[0];

    private final BeanContext beanContext;

    private final AudiobookMapper audiobookMapper;

    private final AudiobookIndex audiobookIndex;

    @Property(name = RepositoryConfigurationKeys.HOERBUCHDIENST_REPOSITORY_LOCALDISK_URI)
    private Path repositoryUri;

    @Inject
    AudiobookRepositoryImpl(final BeanContext beanContext,
                            final AudiobookMapper audiobookMapper,
                            final AudiobookIndex audiobookIndex) {
        this.beanContext = beanContext;
        this.audiobookMapper = audiobookMapper;
        this.audiobookIndex = audiobookIndex;
    }

    @Override
    public String[] findAll(String keyword) {
        return audiobookIndex.findAll(keyword);
    }

    @Override
    public Audiobook find(String titelnummer) {
        return audiobookMapper.audiobook(titelnummer);
    }

    @Override
    public byte[] track(final String titelnummer, final String ident) {
        final AudiobookStreamResolver audiobookStreamResolver = beanContext.createBean(AudiobookStreamResolver.class,
                titelnummer);
        final InputStream trackAsStream = audiobookStreamResolver.trackAsStream(titelnummer, ident);
        final Path tempMp3 = repositoryUri.resolve(String.format("%sKapitel", titelnummer)).resolve(ident);
        try (final OutputStream tempMp3Stream = Files.newOutputStream(tempMp3, StandardOpenOption.CREATE)) {
            trackAsStream.transferTo(tempMp3Stream);
        } catch (IOException e) {
            throw new AudiobookRepositoryException(e);
        }
        try {
            final Mp3File mp3file = new Mp3File(tempMp3);
            mp3file.getId3v1Tag().setComment("WBH-123456"); // max 30 Zeichen
            final Path other = tempMp3.resolve(".watermark");
            mp3file.save(other.toAbsolutePath().toString());
            return Files.readAllBytes(other);
        } catch (IOException | NotSupportedException | UnsupportedTagException | InvalidDataException e) {
            throw new AudiobookRepositoryException(e);
        }
    }

}

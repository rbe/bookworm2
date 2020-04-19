/*
 * Copyright (C) 2011-2020 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchdienst.adapter.required.daisyaudiobook;

import javax.inject.Inject;

import io.micronaut.context.BeanContext;
import io.micronaut.test.annotation.MicronautTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import wbh.bookworm.hoerbuchdienst.domain.required.audiobook.Audiobook;
import wbh.bookworm.hoerbuchdienst.domain.required.audiobook.AudiobookMapper;

@MicronautTest
class AudiobookMapperImplTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(AudiobookMapperImplTest.class);

    private static final String TITLENUMMER = "32909";

    @Inject
    private BeanContext beanContext;

    @Inject
    private AudiobookMapper audiobookMapper;

    @Test
    void shouldReadDaisyAudiobookByAudiobookMapper() {
        final Audiobook audiobook = audiobookMapper.audiobook(TITLENUMMER);
        Assertions.assertNotNull(audiobook);
        Assertions.assertEquals("Halt die Wolken fest", audiobook.getTitle());
        Assertions.assertEquals("Stiller, Dorothea", audiobook.getAuthor());
        Assertions.assertEquals("WBH", audiobook.getPublisher());
        Assertions.assertEquals("2020", audiobook.getDate());
        Assertions.assertEquals("2018", audiobook.getSourceDate());
        Assertions.assertEquals("Love Birds Stuttgart", audiobook.getSourcePublisher());
        Assertions.assertEquals("Seifert, Jutta", audiobook.getNarrator());
        Assertions.assertEquals(audiobook.getTocItems(), audiobook.getAudiotracks().size());
        LOGGER.info("{}", audiobook);
    }

    @Test
    void shouldReadDaisyAudiobookByLocalDiskAudiobookStreamResolver() {
        final LocalDiskAudiobookStreamResolverImpl audiobookStreamResolver = beanContext.createBean(
                LocalDiskAudiobookStreamResolverImpl.class);
        final AudiobookMapperImpl audiobookMapper = new AudiobookMapperImpl(audiobookStreamResolver);
        final Audiobook audiobook = audiobookMapper.createAudiobook(TITLENUMMER, audiobookStreamResolver);
        Assertions.assertNotNull(audiobook);
        Assertions.assertEquals("Halt die Wolken fest", audiobook.getTitle());
        Assertions.assertEquals("Stiller, Dorothea", audiobook.getAuthor());
        Assertions.assertEquals("WBH", audiobook.getPublisher());
        Assertions.assertEquals("2020", audiobook.getDate());
        Assertions.assertEquals("2018", audiobook.getSourceDate());
        Assertions.assertEquals("Love Birds Stuttgart", audiobook.getSourcePublisher());
        Assertions.assertEquals("Seifert, Jutta", audiobook.getNarrator());
        Assertions.assertEquals(audiobook.getTocItems(), audiobook.getAudiotracks().size());
        LOGGER.info("{}", audiobook);
    }

    @Test
    void shouldReadDaisyAudiobookByObjectStorageAudiobookStreamResolver() {
        final ObjectStorageAudiobookStreamResolverImpl audiobookStreamResolver = beanContext.createBean(
                ObjectStorageAudiobookStreamResolverImpl.class, "minio");
        final AudiobookMapperImpl audiobookMapper = new AudiobookMapperImpl(audiobookStreamResolver);
        final Audiobook audiobook = audiobookMapper.createAudiobook(TITLENUMMER, audiobookStreamResolver);
        Assertions.assertNotNull(audiobook);
        Assertions.assertEquals("Halt die Wolken fest", audiobook.getTitle());
        Assertions.assertEquals("Stiller, Dorothea", audiobook.getAuthor());
        Assertions.assertEquals("WBH", audiobook.getPublisher());
        Assertions.assertEquals("2020", audiobook.getDate());
        Assertions.assertEquals("2018", audiobook.getSourceDate());
        Assertions.assertEquals("Love Birds Stuttgart", audiobook.getSourcePublisher());
        Assertions.assertEquals("Seifert, Jutta", audiobook.getNarrator());
        Assertions.assertEquals(audiobook.getTocItems(), audiobook.getAudiotracks().size());
        LOGGER.info("{}", audiobook);
    }

}

/*
 * Copyright (C) 2011-2020 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchdienst.adapter.required.daisy.smilmapper;

import javax.inject.Inject;
import javax.inject.Named;

import io.micronaut.test.annotation.MicronautTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.smil10.Ref;

import wbh.bookworm.hoerbuchdienst.adapter.required.daisy.streamresolver.AudiobookStreamResolver;
import wbh.bookworm.hoerbuchdienst.domain.required.audiobookrepository.Audiobook;
import wbh.bookworm.hoerbuchdienst.domain.required.audiobookrepository.AudiobookMapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@MicronautTest
class AudiobookMapperImplTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(AudiobookMapperImplTest.class);

    private static final String TITLENUMMER = "32909";

    @Inject
    private AudiobookMapper audiobookMapper;

    @Inject
    @Named("localdisk")
    private AudiobookStreamResolver localDiskAudiobookStreamResolver;

    @Test
    void shouldReadDaisyAudiobook() {
        final Audiobook audiobook = audiobookMapper.audiobook(TITLENUMMER);
        Assertions.assertNotNull(audiobook);
        assertEquals("Halt die Wolken fest", audiobook.getTitle());
        assertEquals("Stiller, Dorothea", audiobook.getAuthor());
        assertEquals("WBH", audiobook.getPublisher());
        assertEquals("2020", audiobook.getDate());
        assertEquals("2018", audiobook.getSourceDate());
        assertEquals("Love Birds Stuttgart", audiobook.getSourcePublisher());
        assertEquals("Seifert, Jutta", audiobook.getNarrator());
        assertEquals(audiobook.getTocItems(), audiobook.getAudiotracks().size());
        LOGGER.info("{}", audiobook);
    }

    @Test
    void shouldGetFilenameWithHashtag() {
        final Ref ref = new Ref();
        ref.setSrc("nlzt0002.smil#rxtx_1234");
        final String src = ((AudiobookMapperImpl) audiobookMapper).filenameFromSrc(ref);
        assertEquals("nlzt0002.smil", src);
    }

    @Test
    void shouldGetFilenameWithoutHashtag() {
        final Ref ref = new Ref();
        ref.setSrc("nlzt0002.smil");
        final String src = ((AudiobookMapperImpl) audiobookMapper).filenameFromSrc(ref);
        assertEquals("nlzt0002.smil", src);
    }

    @Test
    void shouldReadDaisyAudiobookByLocalDiskAudiobookStreamResolver() {
        final AudiobookMapperImpl audiobookMapper = new AudiobookMapperImpl(localDiskAudiobookStreamResolver);
        final Audiobook audiobook = audiobookMapper.createAudiobook(TITLENUMMER, localDiskAudiobookStreamResolver);
        assertNotNull(audiobook);
        assertEquals("Halt die Wolken fest", audiobook.getTitle());
        assertEquals("Stiller, Dorothea", audiobook.getAuthor());
        assertEquals("WBH", audiobook.getPublisher());
        assertEquals("2020", audiobook.getDate());
        assertEquals("2018", audiobook.getSourceDate());
        assertEquals("Love Birds Stuttgart", audiobook.getSourcePublisher());
        assertEquals("Seifert, Jutta", audiobook.getNarrator());
        assertEquals(audiobook.getTocItems(), audiobook.getAudiotracks().size());
        LOGGER.info("{}", audiobook);
    }

}

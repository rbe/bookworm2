/*
 * Copyright (C) 2011-2020 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchdienst.adapter.required.daisyaudiobook;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import wbh.bookworm.hoerbuchdienst.domain.required.audiobook.Audiobook;
import wbh.bookworm.hoerbuchdienst.domain.required.audiobook.AudiobookMapper;

class AudiobookMapperTest {

    @Test
    void shouldReadDaisyAudiobook() {
        final AudiobookMapper audiobookMapper = new AudiobookMapperImpl();
        final Audiobook audiobook = audiobookMapper.from("32909Kapitel");
        Assertions.assertNotNull(audiobook);
        Assertions.assertEquals("Halt die Wolken fest", audiobook.getTitle());
        Assertions.assertEquals("Stiller, Dorothea", audiobook.getAuthor());
        Assertions.assertEquals("WBH", audiobook.getPublisher());
        Assertions.assertEquals("2020", audiobook.getDate());
        Assertions.assertEquals("2018", audiobook.getSourceDate());
        Assertions.assertEquals("Love Birds Stuttgart", audiobook.getSourcePublisher());
        Assertions.assertEquals("Seifert, Jutta", audiobook.getNarrator());
        Assertions.assertEquals(audiobook.getTocItems(), audiobook.getAudiotracks().length);
        System.out.println(audiobook);
    }

}

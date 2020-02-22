/*
 * Copyright (C) 2011-2020 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchdienst.infrastructure.smil;

import org.junit.jupiter.api.Test;

import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class AudiobookFactoryTest {

    @Test
    void shouldReadDaisyAudiobook() {
        final String base = "src/main/resources";
        final AudiobookFactory audiobookFactory = new AudiobookFactory(Path.of(base));
        final Audiobook audiobook = audiobookFactory.fromDirectory("32909Kapitel");
        assertNotNull(audiobook);
        assertEquals("Halt die Wolken fest", audiobook.getTitle());
        assertEquals("Stiller, Dorothea", audiobook.getAuthor());
        assertEquals("WBH", audiobook.getPublisher());
        assertEquals("2020", audiobook.getDate());
        assertEquals("2018", audiobook.getSourceDate());
        assertEquals("Love Birds Stuttgart", audiobook.getSourcePublisher());
        assertEquals("Seifert, Jutta", audiobook.getNarrator());
        assertEquals(audiobook.getTocItems(), audiobook.getTracks().length);
        System.out.println(audiobook);
    }

}

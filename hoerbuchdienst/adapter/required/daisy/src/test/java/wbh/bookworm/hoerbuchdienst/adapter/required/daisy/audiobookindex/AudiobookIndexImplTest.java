/*
 * Copyright (C) 2011-2020 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchdienst.adapter.required.daisy.audiobookindex;

import javax.inject.Inject;

import io.micronaut.test.annotation.MicronautTest;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import wbh.bookworm.hoerbuchdienst.domain.required.audiobookindex.AudiobookIndex;

import static org.junit.jupiter.api.Assertions.assertTrue;

@MicronautTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class AudiobookIndexImplTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(AudiobookIndexImpl.class);

    @Inject
    private AudiobookIndex audiobookIndex;

    @Test
    @Order(1)
    void shouldIndex() {
        assertTrue(audiobookIndex.index());
    }

    @Test
    @Order(2)
    void shouldFindAll() {
        final String[] all = audiobookIndex.findAll(new String[]{"die"});
        LOGGER.info("{}", all);
        assertTrue(0 < all.length);
    }

}

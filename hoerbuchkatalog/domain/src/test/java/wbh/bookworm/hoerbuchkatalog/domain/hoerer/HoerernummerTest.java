/*
 * Copyright (C) 2011-2018 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchkatalog.domain.hoerer;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(SpringExtension.class)
class HoerernummerTest {

    @Test
    void shouldBekannt() {
        assertTrue(new Hoerernummer("80170").isBekannt());
        assertFalse(new Hoerernummer("00000").isBekannt());
        assertFalse(Hoerernummer.UNBEKANNT.isBekannt());
    }

    @Test
    void shouldUnbekannt() {
        assertFalse(new Hoerernummer("80170").isUnbekannt());
        assertTrue(new Hoerernummer("00000").isUnbekannt());
        assertTrue(Hoerernummer.UNBEKANNT.isUnbekannt());
    }

}

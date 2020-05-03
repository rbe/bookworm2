/*
 * Copyright (C) 2011-2018 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchkatalog.domain.katalog;

import java.time.LocalDate;
import java.util.TreeSet;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import wbh.bookworm.shared.domain.AghNummer;
import wbh.bookworm.shared.domain.Sachgebiet;
import wbh.bookworm.shared.domain.Titelnummer;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;

@ExtendWith(SpringExtension.class)
class HoerbuchkatalogTest {

    @Test
    void shouldHaveSortedSet() {
        final TreeSet<Hoerbuch> hoerbucher = new TreeSet<>(Hoerbuch::compareTo);
        final Hoerbuch hoerbuch1 = new Hoerbuch(Sachgebiet.A, new Titelnummer("1"),
                "", "", "", "",
                "", "", "", "", "", "",
                "", "", "", "", "",
                LocalDate.now(), new AghNummer("1-1234567-2-3"), false);
        hoerbucher.add(hoerbuch1);
        final Hoerbuch hoerbuch2 = new Hoerbuch(Sachgebiet.B, new Titelnummer("2"),
                "", "", "", "",
                "", "", "", "", "", "",
                "", "", "", "", "",
                LocalDate.now(), new AghNummer("1-1234567-2-4"), false);
        hoerbucher.add(hoerbuch2);
        final Hoerbuch hoerbuch3 = new Hoerbuch(Sachgebiet.C, new Titelnummer("3"),
                "", "", "", "",
                "", "", "", "", "", "",
                "", "", "", "", "",
                LocalDate.now(), new AghNummer("1-1234567-2-5"), false);
        hoerbucher.add(hoerbuch3);
        assertNull(hoerbucher.higher(hoerbuch1));
        assertSame(hoerbucher.higher(hoerbuch2), hoerbuch1);
        assertSame(hoerbucher.higher(hoerbuch3), hoerbuch2);
        assertSame(hoerbucher.lower(hoerbuch1), hoerbuch2);
        assertSame(hoerbucher.lower(hoerbuch2), hoerbuch3);
        assertNull(hoerbucher.lower(hoerbuch3));
    }

    private void dumpHoerbuecherStruktur(final TreeSet<Hoerbuch> hoerbucher) {
        hoerbucher.forEach(h -> {
            String lower = null != hoerbucher.lower(h)
                    ? hoerbucher.lower(h).getTitelnummer().getValue() : "unbekannt";
            String higher = null != hoerbucher.higher(h)
                    ? hoerbucher.higher(h).getTitelnummer().getValue() : "unbekannt";
            System.out.printf("%s lower = %s%n", h.getTitelnummer().getValue(), lower);
            System.out.printf("%s higher = %s%n", h.getTitelnummer().getValue(), higher);
        });
    }

}

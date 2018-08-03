/*
 * Copyright (C) 2011-2018 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchkatalog.domain.test;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import wbh.bookworm.hoerbuchkatalog.domain.AghNummer;
import wbh.bookworm.hoerbuchkatalog.domain.Hoerbuch;
import wbh.bookworm.hoerbuchkatalog.domain.Sachgebiet;
import wbh.bookworm.hoerbuchkatalog.domain.Titelnummer;

import java.time.LocalDate;
import java.util.TreeSet;

@ExtendWith(SpringExtension.class)
class HoerbuchTest {

    @Test
    void should() {
        TreeSet<Hoerbuch> hoerbucher = new TreeSet<>(Hoerbuch::compareTo);
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
        Assertions.assertTrue(hoerbucher.higher(hoerbuch1) == null);
        Assertions.assertTrue(hoerbucher.higher(hoerbuch2) == hoerbuch1);
        Assertions.assertTrue(hoerbucher.higher(hoerbuch3) == hoerbuch2);
        Assertions.assertTrue(hoerbucher.lower(hoerbuch1) == hoerbuch2);
        Assertions.assertTrue(hoerbucher.lower(hoerbuch2) == hoerbuch3);
        Assertions.assertTrue(hoerbucher.lower(hoerbuch3) == null);
    }

    private void dumpHoerbuecherStruktur(final TreeSet<Hoerbuch> hoerbucher) {
        hoerbucher.forEach(h -> {
            String lower = null != hoerbucher.lower(h) ? hoerbucher.lower(h).getTitelnummer().getValue() : "kein";
            String higher = null != hoerbucher.higher(h) ? hoerbucher.higher(h).getTitelnummer().getValue() : "kein";
            System.out.printf("%s lower = %s%n", h.getTitelnummer().getValue(), lower);
            System.out.printf("%s higher = %s%n", h.getTitelnummer().getValue(), higher);
        });
    }

}

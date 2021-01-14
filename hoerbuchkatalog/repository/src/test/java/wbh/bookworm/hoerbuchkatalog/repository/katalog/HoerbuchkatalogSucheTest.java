/*
 * Copyright (C) 2011-2018 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchkatalog.repository.katalog;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import wbh.bookworm.hoerbuchkatalog.domain.katalog.Suchergebnis;
import wbh.bookworm.hoerbuchkatalog.domain.katalog.Suchparameter;

import aoc.mikrokosmos.lang.strings.StringNormalizer;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(classes = {KatalogTestAppConfig.class})
@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@SuppressWarnings({"squid:S1192"})
class HoerbuchkatalogSucheTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(HoerbuchkatalogSucheTest.class);

    private final Hoerbuchkatalog hoerbuchkatalog;

    @Autowired
    HoerbuchkatalogSucheTest(final Hoerbuchkatalog hoerbuchkatalog) {
        this.hoerbuchkatalog = hoerbuchkatalog;
    }

    @Test
    void shouldFindeHoerbuecherMitStichwortKapital() {
        final Suchparameter suchparameter = new Suchparameter();
        final String stichwort = "Kapital";
        suchparameter.hinzufuegen(Suchparameter.Feld.STICHWORT, stichwort);
        final Suchergebnis sucheregebnis = hoerbuchkatalog.suchen(suchparameter);
        LOGGER.debug("{} Suchergebnisse", sucheregebnis.getAnzahl());
        sucheregebnis.getTitelnummern().forEach(
                titelnummer -> LOGGER.info("Suchergebnis: Titelnummer#{}", titelnummer));
        assertTrue(sucheregebnis.getAnzahl() > 0,
                "Kein Suchergebnis für '" + stichwort + "' im Hörbuchkatalog gefunden");
    }

    @Test
    void shouldFindeHoerbuecherMitStichwortAdler() {
        final Suchparameter suchparameter = new Suchparameter();
        final String stichwort = "Adler";
        suchparameter.hinzufuegen(Suchparameter.Feld.STICHWORT, stichwort);
        final Suchergebnis sucheregebnis = hoerbuchkatalog.suchen(suchparameter);
        LOGGER.info("{} Suchergebnisse", sucheregebnis.getAnzahl());
        sucheregebnis.getTitelnummern().forEach(
                titelnummer -> LOGGER.info("Suchergebnis: Titelnummer#{}", titelnummer));
        assertTrue(sucheregebnis.getAnzahl() > 0,
                "Kein Suchergebnis für '" + stichwort + "' im Hörbuchkatalog gefunden");
    }

    @Test
    void shouldFindeHoerbuecherMitAutor() {
        final Suchparameter suchparameter = new Suchparameter();
        final String wert = "Alfred Hitchcock".toLowerCase();
        suchparameter.hinzufuegen(Suchparameter.Feld.AUTOR, wert);
        final Suchergebnis sucheregebnis = hoerbuchkatalog.suchen(suchparameter);
        LOGGER.info("{} Suchergebnisse", sucheregebnis.getAnzahl());
        sucheregebnis.getTitelnummern().forEach(
                titelnummer -> LOGGER.info("Suchergebnis: Titelnummer#{}", titelnummer));
        assertTrue(sucheregebnis.getAnzahl() > 0,
                "Kein Suchergebnis für '" + wert + "' im Hörbuchkatalog gefunden");
    }

    @Test
    void shouldFindeHoerbuecherMitAutor2() {
        final Suchparameter suchparameter = new Suchparameter();
        //final String wert = "Hitchcock, Alfred".toLowerCase();
        String wert = "Hitchcock,Alfred".toLowerCase();
        wert = StringNormalizer.normalize(wert);
        suchparameter.hinzufuegen(Suchparameter.Feld.AUTOR, wert);
        final Suchergebnis sucheregebnis = hoerbuchkatalog.suchen(suchparameter);
        LOGGER.info("{} Suchergebnisse", sucheregebnis.getAnzahl());
        sucheregebnis.getTitelnummern().forEach(
                titelnummer -> LOGGER.info("Suchergebnis: Titelnummer#{}", titelnummer));
        assertTrue(sucheregebnis.getAnzahl() > 0,
                "Kein Suchergebnis für '" + wert + "' im Hörbuchkatalog gefunden");
    }

    @Test
    void shouldFindeHoerbuecherMitSprecher() {
        final Suchparameter suchparameter = new Suchparameter();
        String wert = "Elke Große-Woestmann".toLowerCase();
        //wert = StringNormalizer.normalize(wert);
        suchparameter.hinzufuegen(Suchparameter.Feld.SPRECHER1, wert);
        final Suchergebnis sucheregebnis = hoerbuchkatalog.suchen(suchparameter);
        LOGGER.info("{} Suchergebnisse", sucheregebnis.getAnzahl());
        sucheregebnis.getTitelnummern().forEach(
                titelnummer -> LOGGER.info("Suchergebnis: Titelnummer#{}", titelnummer));
        assertTrue(sucheregebnis.getAnzahl() > 0,
                "Kein Suchergebnis für '" + wert + "' im Hörbuchkatalog gefunden");
    }

    @Test
    void shouldFindeHoerbuecherMitSprecherAnhandStichwort() {
        final Suchparameter suchparameter = new Suchparameter();
        String wert = "Elke Große-Woestmann".toLowerCase();
        //wert = StringNormalizer.normalize(wert);
        suchparameter.hinzufuegen(Suchparameter.Feld.STICHWORT, wert);
        final Suchergebnis sucheregebnis = hoerbuchkatalog.suchen(suchparameter);
        LOGGER.info("{} Suchergebnisse", sucheregebnis.getAnzahl());
        sucheregebnis.getTitelnummern().forEach(
                titelnummer -> LOGGER.info("Suchergebnis: Titelnummer#{}", titelnummer));
        assertTrue(sucheregebnis.getAnzahl() > 0,
                "Kein Suchergebnis für '" + wert + "' im Hörbuchkatalog gefunden");
    }

}

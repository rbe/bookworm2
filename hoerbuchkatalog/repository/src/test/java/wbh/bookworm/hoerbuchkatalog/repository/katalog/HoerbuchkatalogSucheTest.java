/*
 * Copyright (C) 2011-2018 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchkatalog.repository.katalog;

import wbh.bookworm.hoerbuchkatalog.domain.katalog.Suchergebnis;
import wbh.bookworm.hoerbuchkatalog.domain.katalog.Suchparameter;
import wbh.bookworm.hoerbuchkatalog.repository.config.RepositoryTestAppConfig;

import org.junit.FixMethodOrder;
import org.junit.Ignore;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runners.MethodSorters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@SpringBootTest(classes = {RepositoryTestAppConfig.class})
@ExtendWith(SpringExtension.class)
@FixMethodOrder(MethodSorters.JVM)
@Ignore("TODO Neu mit Factory; Integration in HörbuchkatalogTest")
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
        final Suchergebnis sucheregebnis = hoerbuchkatalog.sucheNachStichwort(stichwort);
        LOGGER.info("{} Suchergebnisse", sucheregebnis.getAnzahl());
        sucheregebnis.getTitelnummern().forEach(
                titelnummer -> LOGGER.info("Suchergebnis: Titelnummer#{}", titelnummer));
        Assertions.assertTrue(sucheregebnis.getAnzahl() > 0,
                "Kein Suchergebnis für '" + stichwort + "' im Hörbuchkatalog gefunden");
    }

    @Test
    void shouldFindeHoerbuecherMitStichwortAdler() {
        final Suchparameter suchparameter = new Suchparameter();
        final String stichwort = "Adler";
        suchparameter.hinzufuegen(Suchparameter.Feld.STICHWORT, stichwort);
        final Suchergebnis sucheregebnis = hoerbuchkatalog.sucheNachStichwort(stichwort);
        LOGGER.info("{} Suchergebnisse", sucheregebnis.getAnzahl());
        sucheregebnis.getTitelnummern().forEach(
                titelnummer -> LOGGER.info("Suchergebnis: Titelnummer#{}", titelnummer));
        Assertions.assertTrue(sucheregebnis.getAnzahl() > 0,
                "Kein Suchergebnis für '" + stichwort + "' im Hörbuchkatalog gefunden");
    }

    @Test
    void shouldFindeHoerbuecherMitMehrerenEingaben() {
        final Suchparameter suchparameter = new Suchparameter();
        final String wert = "Alfred Hitchcock";
        suchparameter.hinzufuegen(Suchparameter.Feld.AUTOR, wert);
        final Suchergebnis sucheregebnis = hoerbuchkatalog.suchen(suchparameter);
        LOGGER.info("{} Suchergebnisse", sucheregebnis.getAnzahl());
        sucheregebnis.getTitelnummern().forEach(
                titelnummer -> LOGGER.info("Suchergebnis: Titelnummer#{}", titelnummer));
        Assertions.assertTrue(sucheregebnis.getAnzahl() > 0,
                "Kein Suchergebnis für '" + wert + "' im Hörbuchkatalog gefunden");
    }

}

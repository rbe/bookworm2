/*
 * Copyright (C) 2011-2018 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchkatalog.repository;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import wbh.bookworm.hoerbuchkatalog.domain.Suchergebnis;
import wbh.bookworm.hoerbuchkatalog.domain.Suchparameter;

@SpringBootTest(classes = {AppConfig.class})
@ExtendWith(SpringExtension.class)
class HoerbuchkatalogSucheTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(HoerbuchkatalogSucheTest.class);

    private final HoerbuchkatalogSuche hoerbuchkatalogSuche;

    @Autowired
    HoerbuchkatalogSucheTest(final HoerbuchkatalogSuche hoerbuchkatalogSuche) {
        this.hoerbuchkatalogSuche = hoerbuchkatalogSuche;
    }

    @Test
    void shouldFindeHoerbuecherMitStichwortKapital() {
        final Suchparameter suchparameter = new Suchparameter();
        final String wert = "*kapital*";
        suchparameter.hinzufuegen(Suchparameter.Feld.STICHWORT, wert);
        final Suchergebnis sucheregebnis = hoerbuchkatalogSuche.sucheNachStichwort(suchparameter);
        LOGGER.info("{} Suchergebnisse", sucheregebnis.getAnzahl());
        sucheregebnis.getTitelnummern().forEach(hoerbuch -> LOGGER.info("Suchergebnis: {}", hoerbuch));
        Assertions.assertTrue(sucheregebnis.getAnzahl() > 0,
                "Kein Suchergebnis für '" + wert + "' im Hörbuchkatalog gefunden");
    }

    @Test
    void shouldFindeHoerbuecherMitStichwortAdler() {
        final Suchparameter suchparameter = new Suchparameter();
        final String wert = "*adler*";
        suchparameter.hinzufuegen(Suchparameter.Feld.STICHWORT, wert);
        final Suchergebnis sucheregebnis = hoerbuchkatalogSuche.sucheNachStichwort(suchparameter);
        LOGGER.info("{} Suchergebnisse", sucheregebnis.getAnzahl());
        sucheregebnis.getTitelnummern().forEach(hoerbuch -> LOGGER.info("Suchergebnis: {}", hoerbuch));
        Assertions.assertTrue(sucheregebnis.getAnzahl() > 0,
                "Kein Suchergebnis für '" + wert + "' im Hörbuchkatalog gefunden");
    }

}

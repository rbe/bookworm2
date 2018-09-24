/*
 * Copyright (C) 2011-2018 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.platform.ddd.repository.search;

import org.junit.FixMethodOrder;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runners.MethodSorters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(classes = {TestAppConfig.class})
@ExtendWith(SpringExtension.class)
@FixMethodOrder(MethodSorters.JVM)
class LuceneIndexTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(LuceneIndexTest.class);

    private final ApplicationContext applicationContext;

    @Autowired
    LuceneIndexTest(final ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Test
    void shouldDeleteIndex() {
        final LuceneIndex luceneIndex = applicationContext.getBean(LuceneIndex.class, "test");
        assertTrue(luceneIndex.deleteIndex());
    }

/*
    @Test
    void shouldFindeHoerbuecherMitStichwortKapital() {
        final Suchparameter suchparameter = new Suchparameter();
        final String wert = "*kapital*";
        suchparameter.hinzufuegen(Suchparameter.Feld.STICHWORT, wert);
        final Suchergebnis sucheregebnis = luceneIndexFactory.sucheNachStichwort(suchparameter);
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
        final Suchergebnis sucheregebnis = luceneIndexFactory.sucheNachStichwort(suchparameter);
        LOGGER.info("{} Suchergebnisse", sucheregebnis.getAnzahl());
        sucheregebnis.getTitelnummern().forEach(hoerbuch -> LOGGER.info("Suchergebnis: {}", hoerbuch));
        Assertions.assertTrue(sucheregebnis.getAnzahl() > 0,
                "Kein Suchergebnis für '" + wert + "' im Hörbuchkatalog gefunden");
    }
*/

}

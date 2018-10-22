/*
 * Copyright (C) 2011-2018 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchkatalog.repository.katalog;

import wbh.bookworm.hoerbuchkatalog.domain.katalog.AghNummer;
import wbh.bookworm.hoerbuchkatalog.repository.config.RepositoryTestAppConfig;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Set;

@SpringBootTest(classes = {RepositoryTestAppConfig.class})
@ExtendWith(SpringExtension.class)
class AghNummernRepositoryTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(AghNummernRepositoryTest.class);

    private final AghNummernRepository aghNummernRepository;

    @Autowired
    AghNummernRepositoryTest(final AghNummernRepository aghNummernRepository) {
        this.aghNummernRepository = aghNummernRepository;
    }

    @Test
    void shouldFindeImportierteAghNummern() throws ImportFailedException {
        final Set<AghNummer> aghNummern = aghNummernRepository.importiereKatalogAusArchiv();
        aghNummern.forEach(a -> {
            LOGGER.info("AGH Nummer {} in Set enthalten: {}", a, aghNummern.contains(a));
        });
    }

}

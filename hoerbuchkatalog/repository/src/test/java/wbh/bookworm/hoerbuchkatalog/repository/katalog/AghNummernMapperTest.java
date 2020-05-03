/*
 * Copyright (C) 2011-2018 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchkatalog.repository.katalog;

import java.util.Set;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import wbh.bookworm.shared.domain.AghNummer;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(classes = {KatalogTestAppConfig.class})
@ExtendWith(SpringExtension.class)
class AghNummernMapperTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(AghNummernMapperTest.class);

    private final AghNummernMapper aghNummernMapper;

    @Autowired
    AghNummernMapperTest(final AghNummernMapper aghNummernMapper) {
        this.aghNummernMapper = aghNummernMapper;
    }

    @Test
    void shouldListeImportierteAghNummern() {
        final Set<AghNummer> aghNummern = aghNummernMapper.importiere();
        aghNummern.forEach(a -> {
            LOGGER.trace("AGH Nummer {} in Set enthalten: {}", a, aghNummern.contains(a));
        });
        assertTrue(aghNummern.size() > 0);
    }

}

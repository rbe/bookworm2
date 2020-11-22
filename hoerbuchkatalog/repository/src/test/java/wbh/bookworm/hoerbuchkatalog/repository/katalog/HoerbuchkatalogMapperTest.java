/*
 * Copyright (C) 2011-2019 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchkatalog.repository.katalog;

import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.text.NumberFormat;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Locale;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.ActiveProfiles;

import wbh.bookworm.hoerbuchkatalog.domain.katalog.Hoerbuch;

import aoc.mikrokosmos.ddd.repository.RepositoryArchive;
import aoc.mikrokosmos.ddd.repository.RepositoryArchiveException;

@ActiveProfiles("test")
class HoerbuchkatalogMapperTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(HoerbuchkatalogMapperTest.class);

    private final RepositoryArchive repositoryArchive;

    HoerbuchkatalogMapperTest() {
        this.repositoryArchive = new RepositoryArchive(Path.of("var/wbh/hoerbuchkatalog"));
    }

    @Test
    void shouldParseGesamtDat() {
        LocalDateTime start = LocalDateTime.now();
        Path path = null;// = Path.of("var/wbh/hoerbuchkatalog/Gesamt.dat").toAbsolutePath();
        try {
            path = repositoryArchive.find(Path.of("Gesamt.dat")).orElseThrow();
        } catch (RepositoryArchiveException e) {
            LOGGER.error("", e);
        }
        final HoerbuchkatalogMapper hoerbuchkatalogMapper = new HoerbuchkatalogMapper();
        final Set<Hoerbuch> hoerbuecher = hoerbuchkatalogMapper.importiere(path, StandardCharsets.ISO_8859_1);
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Importieren von {} Hörbüchern benötigte {}",
                    NumberFormat.getInstance(Locale.GERMANY).format(hoerbuecher.size()),
                    Duration.between(start, LocalDateTime.now()));
        }
    }

}

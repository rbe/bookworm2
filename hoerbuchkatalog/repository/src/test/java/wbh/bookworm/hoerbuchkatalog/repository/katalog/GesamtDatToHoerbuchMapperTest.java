/*
 * Copyright (C) 2011-2019 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchkatalog.repository.katalog;

import wbh.bookworm.hoerbuchkatalog.domain.katalog.Hoerbuch;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.text.NumberFormat;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Locale;
import java.util.Set;

class GesamtDatToHoerbuchMapperTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(GesamtDatToHoerbuchMapperTest.class);

    @Test
    void shouldParseGesamtDat() {
        LocalDateTime start = LocalDateTime.now();
        final Path path = Path.of("/Users/rbe/project/wbh.bookworm/hoerbuchkatalog/repository/src/test/var/hoerbuchkatalog/Gesamt-2018-12-19T13-23-53-53636.dat");
        final GesamtDatToHoerbuchMapper gesamtDatToHoerbuchMapper = new GesamtDatToHoerbuchMapper();
        final Set<Hoerbuch> hoerbuecher = gesamtDatToHoerbuchMapper.importiere(
                path, StandardCharsets.ISO_8859_1);
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Importieren von {} Hörbüchern benötigte {}",
                    NumberFormat.getInstance(Locale.GERMANY).format(hoerbuecher.size()),
                    Duration.between(start, LocalDateTime.now()));
        }
    }

}

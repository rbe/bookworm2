/*
 * Copyright (C) 2011-2019 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchkatalog.repository.nutzerdaten;

import wbh.bookworm.hoerbuchkatalog.domain.hoerer.Hoerer;
import wbh.bookworm.hoerbuchkatalog.domain.hoerer.Hoerernummer;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
import java.nio.file.Path;

class HoererMapperTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(HoererMapperTest.class);

    @Test
    void shouldAlleHoererdatenImportieren() {
        final String string = "/Users/rbe/project/wbh.bookworm/hoerbuchkatalog/repository";
        final Path hoerstp = Path.of(string + "/src/test/var/nutzerdaten/hoerstp.csv");
        final Path hoebstp = Path.of(string + "/src/test/var/nutzerdaten/hoebstp.csv");
        final Path hoekzstp = Path.of(string + "/src/test/var/nutzerdaten/hoekzstp.csv");
        final HoererMapper hoererMapper = new HoererMapper(hoerstp, hoekzstp, hoebstp,
                StandardCharsets.ISO_8859_1, 9_000);
        /*
        System.out.println(csvParser.maybeGetValue(0, "HOENR"));
        System.out.println(csvParser.maybeGetValue(0, "HÖNR"));
        System.out.println(csvParser.getValue(0, "HOERER NR."));
        System.out.println(csvParser.getValue(0, "HÖRER NR."));
        */
        /*
        LocalDateTime start = LocalDateTime.now();
        final String[] hoenrs = hoererMapper.hoerstp.getColumn("HOENR");
        System.out.println("getColumn: " + hoenrs.length
                + ": " + Duration.between(start, LocalDateTime.now()).toMillis());
        */
        final Hoerernummer hoerer80170 = new Hoerernummer("80170");
        final Hoerer hoerer = hoererMapper.hoerer(hoerer80170);
        LOGGER.debug("hoerer(80170): {}", hoerer);
        final Hoerernummer hoerer483 = new Hoerernummer("483");
        LOGGER.debug("{}", hoererMapper.belastungenFuer(hoerer483));
    }

}

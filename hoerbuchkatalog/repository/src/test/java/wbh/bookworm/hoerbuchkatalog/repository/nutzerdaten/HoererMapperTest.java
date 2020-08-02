/*
 * Copyright (C) 2011-2019 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchkatalog.repository.nutzerdaten;

import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.concurrent.ExecutorService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import wbh.bookworm.hoerbuchkatalog.domain.hoerer.Hoerer;
import wbh.bookworm.shared.domain.mandant.Hoerernummer;

@SpringBootTest(classes = {NutzerdatenAppConfig.class})
@ExtendWith(SpringExtension.class)
class HoererMapperTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(HoererMapperTest.class);

    private final ExecutorService executorService;

    private final HoererMapper hoererMapper;

    @Autowired
    HoererMapperTest(final ExecutorService executorService,
                     final HoererMapper hoererMapper) {
        this.executorService = executorService;
        this.hoererMapper = hoererMapper;
    }

    @BeforeEach
    void beforeAll() {
        final String string = "/Users/rbe/project/wbh.bookworm/hoerbuchkatalog/repository";
        /* TODO Konfiguration */final Path hoerstp = Path.of(string + "/src/test/var/wbh/nutzerdaten/hoerstp.csv");
        /* TODO Konfiguration */final Path hoebstp = Path.of(string + "/src/test/var/wbh/nutzerdaten/hoebstp.csv");
        /* TODO Konfiguration */final Path hoekzstp = Path.of(string + "/src/test/var/wbh/nutzerdaten/hoekzstp.csv");
        hoererMapper.leseAs400Dateien(executorService,
                StandardCharsets.ISO_8859_1, 9_000,
                hoerstp, hoekzstp, hoebstp);
    }

    /* TODO CsvParserTest
    @Test
    void shouldGetValue() {
        System.out.println(csvParser.maybeGetValue(0, "HOENR"));
        System.out.println(csvParser.maybeGetValue(0, "HÖNR"));
        System.out.println(csvParser.getValue(0, "HOERER NR."));
        System.out.println(csvParser.getValue(0, "HÖRER NR."));
    }
    */

    /* TODO CsvParserTest
    @Test
    void shouldGetColumn() {
        LocalDateTime start = LocalDateTime.now();
        final String[] hoenrs = csvParser.getColumn("HOENR");
        System.out.println("getColumn: " + hoenrs.length
                + ": " + Duration.between(start, LocalDateTime.now()).toMillis());
    }
    */

    @Test
    void shouldAlleHoererdatenImportieren() {
        final Hoerernummer hoerer80170 = new Hoerernummer("80170");
        final Hoerer hoerer = hoererMapper.hoerer(hoerer80170);
        LOGGER.debug("hoerer(80170): {}", hoerer);
        final Hoerernummer hoerer483 = new Hoerernummer("483");
        LOGGER.debug("{}", hoererMapper.belastungenFuer(hoerer483));
    }

}

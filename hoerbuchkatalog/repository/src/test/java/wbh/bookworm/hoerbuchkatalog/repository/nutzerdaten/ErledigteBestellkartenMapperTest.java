/*
 * Copyright (C) 2011-2019 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchkatalog.repository.nutzerdaten;

import wbh.bookworm.hoerbuchkatalog.domain.hoerer.Hoerernummer;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@SpringBootTest(classes = {NutzerdatenTestAppConfig.class})
@ExtendWith(SpringExtension.class)
class ErledigteBestellkartenMapperTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(HoererMapperTest.class);

    private final ErledigteBestellkartenMapper erledigteBestellkartenMapper;

    @Autowired
    ErledigteBestellkartenMapperTest(final ErledigteBestellkartenMapper erledigteBestellkartenMapper) {
        this.erledigteBestellkartenMapper = erledigteBestellkartenMapper;
    }

    @Test
    void shouldAlleErledigtenBestellkartenFuerHoererFinden() {
        erledigteBestellkartenMapper.erledigteBestellkartenFuer(new Hoerernummer("80170"));
    }

}

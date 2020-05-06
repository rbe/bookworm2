/*
 * Copyright (C) 2011-2019 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchkatalog.repository.lieferung;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import wbh.bookworm.hoerbuchkatalog.domain.lieferung.ErledigteBestellkarte;
import wbh.bookworm.shared.domain.hoerer.Hoerernummer;

@SpringBootTest(classes = {LieferungAppConfig.class})
@ExtendWith(SpringExtension.class)
class ErledigteBestellkartenMapperTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(ErledigteBestellkartenMapperTest.class);

    //private final ExecutorService executorService;

    private final ErledigteBestellkartenMapper erledigteBestellkartenMapper;

    @Autowired
    ErledigteBestellkartenMapperTest(/*final ExecutorService executorService,*/
                                     final ErledigteBestellkartenMapper erledigteBestellkartenMapper) {
        //this.executorService = executorService;
        this.erledigteBestellkartenMapper = erledigteBestellkartenMapper;
    }

    @Test
    void shouldAlleErledigtenBestellkartenFuerHoererFinden() {
        final List<ErledigteBestellkarte> fuerHoerer80170 =
                erledigteBestellkartenMapper.erledigteBestellkartenFuer(new Hoerernummer("80170"));
        LOGGER.info("{}", fuerHoerer80170);
        //executorService.shutdownNow();
    }

}

/*
 * Copyright (C) 2011-2018 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchkatalog.infrastructure.blista.restdlskatalog;

import wbh.bookworm.hoerbuchkatalog.infrastructure.blista.lieferung.DlsLieferungTestAppConfig;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.IOException;
import java.io.InputStream;

@SpringBootTest(classes = {DlsLieferungTestAppConfig.class})
@ExtendWith(SpringExtension.class)
class RestServiceClientTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(RestServiceClientTest.class);

    @Test
    void shouldFehlermeldungAuswerten() throws IOException {
        final InputStream inputStream = RestServiceClientTest.class
                .getResourceAsStream("/blista-xml/fehler207.xml");
        final DlsAntwort dlsAntwort = RestServiceClient
                .werteAntwortAus(inputStream.readAllBytes());
    }

}

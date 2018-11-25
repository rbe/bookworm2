/*
 * Copyright (C) 2011-2018 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchkatalog.infrastructure.blista.bestellung;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(classes = {BestellungTestAppConfig.class})
//@ContextConfiguration(classes = {BestellungTestAppConfig.class})
@ExtendWith(SpringExtension.class)
class BilletSenderTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(BilletSenderTest.class);

    private static final String USER_ID = "titusTest";

    private static final String AGH_NUMMER = "1-0000122-3-9";

    private final BilletSender billetSender;

    @Autowired
    BilletSenderTest(final BilletSender billetSender) {
        this.billetSender = billetSender;
    }

    @Test
    void shouldCreateBillet() {
        final Billet billet = Billet.create(USER_ID, AGH_NUMMER);
        final String xml = billetSender.marshal(billet);
        assertEquals("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>" +
                "<billet>" +
                "<UserID>titusTest</UserID>" +
                "<BibliothekID>wbh06</BibliothekID>" +
                "<Bestellnummer>1-0000122-3-9</Bestellnummer>" +
                "<Abrufkennwort>"+billet.getAbrufkennwort()+"</Abrufkennwort>" +
                "</billet>", xml);
    }

}


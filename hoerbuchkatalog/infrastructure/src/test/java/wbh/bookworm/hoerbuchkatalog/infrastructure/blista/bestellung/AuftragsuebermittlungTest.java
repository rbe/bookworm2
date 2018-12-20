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

@SpringBootTest(classes = {DlsBestellungTestAppConfig.class})
@ExtendWith(SpringExtension.class)
class AuftragsuebermittlungTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(AuftragsuebermittlungTest.class);

    private static final String USER_ID = "titusTest";

    private static final String AGH_NUMMER = "1-0000122-3-9";

    private final Auftragsuebermittlung auftragsuebermittlung;

    @Autowired
    AuftragsuebermittlungTest(final Auftragsuebermittlung auftragsuebermittlung) {
        this.auftragsuebermittlung = auftragsuebermittlung;
    }

    @Test
    void shouldBilletErstellen() {
        final Auftragsuebermittlung.Billet billet =
                Auftragsuebermittlung.Billet.erstellen(USER_ID, AGH_NUMMER);
        final String xml = auftragsuebermittlung.marshal(billet);
        assertEquals("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>" +
                "<billet>" +
                "<UserID>titusTest</UserID>" +
                "<BibliothekID>wbh06</BibliothekID>" +
                "<Bestellnummer>1-0000122-3-9</Bestellnummer>" +
                "<Abrufkennwort>"+billet.getAbrufkennwort()+"</Abrufkennwort>" +
                "</billet>", xml);
    }

}


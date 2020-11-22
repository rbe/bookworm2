/*
 * Copyright (C) 2011-2018 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchkatalog.infrastructure.blista.bestellung;

import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(classes = {DlsBestellungTestAppConfig.class})
@SpringBootConfiguration
@ExtendWith(SpringExtension.class)
@Disabled
class AuftragsuebermittlungTest {

    private static final String USER_ID = "titusTest";

    private final Auftragsuebermittlung auftragsuebermittlung;

    @Autowired
    AuftragsuebermittlungTest(final Auftragsuebermittlung auftragsuebermittlung) {
        this.auftragsuebermittlung = auftragsuebermittlung;
    }

    /*
    @Test
    void shouldBilletErstellen() {
        final Auftragsuebermittlung.Billet billet =
                Auftragsuebermittlung.auftragErstellen(USER_ID, AGH_NUMMER);
        final String xml = auftragsuebermittlung.marshal(billet);
        assertEquals("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>" +
                "<billet>" +
                "<UserID>titusTest</UserID>" +
                "<BibliothekID>wbh06</BibliothekID>" +
                "<Bestellnummer>1-0000122-3-9</Bestellnummer>" +
                "<Abrufkennwort>" + billet.getAbrufkennwort() + "</Abrufkennwort>" +
                "</billet>", xml);
    }
    */

    @Test
    @Disabled("Keine Aufträge im Produktionssystem durch Tests auslösen")
    void shouldFindProcessed() throws InterruptedException {
        final String userId = USER_ID;
        final String aghNummer = "1-0000122-3-9";
        auftragsuebermittlung.uebergeben(userId, aghNummer);
        TimeUnit.SECONDS.sleep(5);
        assertEquals(Auftragsstatus.VERARBEITET,
                auftragsuebermittlung.auftragsstatus(userId, aghNummer));
    }

    @Test
    @Disabled
    void shouldFindRejected() throws InterruptedException {
        final String aghNummer = "1-2345678-9-0";
        auftragsuebermittlung.uebergeben(USER_ID, aghNummer);
        TimeUnit.SECONDS.sleep(5);
        assertEquals(Auftragsstatus.ABGELEHNT,
                auftragsuebermittlung.auftragsstatus(USER_ID, aghNummer));
    }

}

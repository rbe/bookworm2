/*
 * Copyright (C) 2011-2018 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchkatalog.infrastructure.blista.bestellung;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(classes = {DlsBestellungTestAppConfig.class})
@SpringBootConfiguration
@ExtendWith(SpringExtension.class)
class DlsBestellungTest {

    private static final String USER_ID = "titusTest";

    private final DlsBestellung dlsBestellung;

    @Autowired
    DlsBestellungTest(final DlsBestellung dlsBestellung) {
        this.dlsBestellung = dlsBestellung;
    }

    @Test
    @Disabled("Benutzername/Passwort in blista-dls.properties")
    void shouldPruefeBestellungUnsinnigerAghNummer() {
        final String aghNummer = "1-2345678-9-0";
        final Auftragsquittung bestellungMoeglich = dlsBestellung.pruefen(USER_ID, aghNummer);
        assertFalse(bestellungMoeglich.isPruefungOk());
    }

    @Test
    @Disabled("Benutzername/Passwort in blista-dls.properties")
    void shouldPruefeBestellungKorrekterAghNummer() {
        final String aghNummer = "1-0000122-3-9";
        final Auftragsquittung bestellungMoeglich = dlsBestellung.pruefen(USER_ID, aghNummer);
        assertTrue(bestellungMoeglich.isPruefungOk());
    }

}

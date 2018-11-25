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
class DlsBestellungTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(DlsBestellungTest.class);

    private static final String USER_ID = "titusTest";

    private static final String AGH_NUMMER = "1-0000122-3-9";

    private final BilletSender billetSender;

    @Autowired
    DlsBestellungTest(final BilletSender billetSender) {
        this.billetSender = billetSender;
    }

/*
    @Test
    @Ignore
    void shouldPutFileIntoNew() throws InterruptedException {
        final String userId = USER_ID;
        final String aghNummer = AGH_NUMMER;
        billetSender.sendToServer(userId, aghNummer);
        Thread.sleep(5 * 1000);
        assertEquals(BilletStatus.PROCESSED,
                billetSender.billetStatus(userId, aghNummer));
    }
*/

/*
    @Test
    void shouldFindProcessed() throws InterruptedException {
        billetSender.sendToServer(USER_ID, AGH_NUMMER);
        Thread.sleep(5 * 1_000L);
        final Billet billet = Billet.create(USER_ID, AGH_NUMMER);
        assertEquals(BilletStatus.PROCESSED,
                billetSender.billetStatus(billet.getUserId(), billet.getAghNummer()));
    }
*/

    @Test
    void shouldFindRejected() throws InterruptedException {
        billetSender.sendToServer(USER_ID, AGH_NUMMER);
        Thread.sleep(5 * 1_000L);
        final Billet billet = Billet.create(USER_ID, AGH_NUMMER);
        assertEquals(BilletStatus.REJECTED,
                billetSender.billetStatus(billet.getUserId(), billet.getAghNummer()));
    }

}


/*
 * Copyright (C) 2011-2018 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchkatalog.app.bestellung;

import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Set;

import com.icegreen.greenmail.junit5.GreenMailExtension;
import com.icegreen.greenmail.user.UserException;
import com.icegreen.greenmail.util.ServerSetup;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import wbh.bookworm.hoerbuchkatalog.app.config.TestAppConfig;
import wbh.bookworm.hoerbuchkatalog.domain.bestellung.Bestellung;
import wbh.bookworm.hoerbuchkatalog.domain.bestellung.BestellungAufgegeben;
import wbh.bookworm.hoerbuchkatalog.domain.bestellung.BestellungId;
import wbh.bookworm.hoerbuchkatalog.domain.hoerer.HoererEmail;
import wbh.bookworm.hoerbuchkatalog.domain.hoerer.Hoerername;
import wbh.bookworm.hoerbuchkatalog.domain.hoerer.Nachname;
import wbh.bookworm.hoerbuchkatalog.domain.hoerer.Vorname;
import wbh.bookworm.shared.domain.hoerbuch.Titelnummer;
import wbh.bookworm.shared.domain.mandant.Hoerernummer;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(classes = {TestAppConfig.class})
@ExtendWith({SpringExtension.class})
class CdBestellungAufgegebenHandlerTest {

    private final CdBestellungAufgegebenHandler cdBestellungAufgegebenHandler;

    private static ServerSetup serverSetup;

    @RegisterExtension
    static GreenMailExtension greenMail;

    static {
        serverSetup = new ServerSetup(8025, "127.0.0.1", "smtp")
                .setVerbose(true);
        greenMail = new GreenMailExtension(
                serverSetup);
    }

    @Autowired
    CdBestellungAufgegebenHandlerTest(final CdBestellungAufgegebenHandler cdBestellungAufgegebenHandler) {
        this.cdBestellungAufgegebenHandler = cdBestellungAufgegebenHandler;
    }

    @BeforeEach
    void beforeEach() throws UserException {
        greenMail.getManagers().getUserManager()
                .createUser("user@example.com", "username", "password");
    }

    @AfterEach
    void afterEach() {
        greenMail.stop();
    }

    @Test
    void shouldBuildBestellbestaetigungCd() throws IOException {
        final Bestellung bestellung = new Bestellung(
                new BestellungId("1234567890"),
                new Hoerernummer("80170"),
                new Hoerername(new Vorname("Herbert"), new Nachname("Hörer")),
                new HoererEmail("herbert.hoerer@example.com"),
                "Hier wurde eine Bemerkung eingegeben.",
                Boolean.FALSE, Boolean.FALSE,
                Set.of(new Titelnummer("69404")/* TODO null, new Titelnummer("789012")*/),
                null,
                LocalDateTime.now()
        );
        cdBestellungAufgegebenHandler.handleEvent(
                new BestellungAufgegeben(new Hoerernummer("80170"), bestellung));
        // TODO assertMessageReceived
        assertMessageReceived();
    }

    private void assertMessageReceived(/* TODO final Email email*/) /*throws MessagingException, IOException*/ {
        final MimeMessage[] receivedMessages = greenMail.getReceivedMessages();
        assertEquals(/* Zwei Empfänger */2, receivedMessages.length);
        /*
        final MimeMessage current = receivedMessages[0];
        assertEquals(email.getSubject(), current.getSubject());
        assertEquals(email.getTo(), current.getAllRecipients()[0].toString());
        assertTrue(String.valueOf(current.getContent()).contains(email.getContent()));
        */
    }

}

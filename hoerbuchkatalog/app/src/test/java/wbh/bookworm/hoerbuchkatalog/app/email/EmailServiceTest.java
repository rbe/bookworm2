/*
 * Copyright (C) 2011-2018 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchkatalog.app.email;

import wbh.bookworm.hoerbuchkatalog.domain.email.Email;
import wbh.bookworm.hoerbuchkatalog.repository.email.EmailRepository;

import com.icegreen.greenmail.junit5.GreenMailExtension;
import com.icegreen.greenmail.user.UserException;
import com.icegreen.greenmail.util.GreenMailUtil;
import com.icegreen.greenmail.util.ServerSetup;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(classes = {EmailTestAppConfig.class})
@ExtendWith({SpringExtension.class})
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class EmailServiceTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(EmailServiceTest.class);

    private final EmailService emailService;

    private final EmailRepository emailRepository;

    private static ServerSetup serverSetup;

    @RegisterExtension
    static GreenMailExtension greenMail;

    static {
        serverSetup = new ServerSetup(8025, "127.0.0.1", "smtp")
                .setVerbose(true);
        greenMail = new GreenMailExtension(serverSetup);
    }

    @Autowired
    EmailServiceTest(final EmailService emailService, final EmailRepository emailRepository) {
        this.emailService = emailService;
        this.emailRepository = emailRepository;
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
    @DisplayName("Text-E-Mail versenden")
    void shouldSendTextEmail() {
        GreenMailUtil.sendTextEmail("wbh@example.com",
                "kunde@example.com", "Some subject",
                "some body", serverSetup);
        final MimeMessage[] receivedMessages = greenMail.getReceivedMessages();
        final MimeMessage receivedMessage = receivedMessages[0];
        assertEquals("some body", GreenMailUtil.getBody(receivedMessage));
    }

    @Test
    @DisplayName("HTML-E-Email senden")
    void shouldSendMimeMessage() throws MessagingException, IOException {
        final Email email = emailRepository.erstellen(
                "from@example.com", "to@example.com",
                "Subject",
                "This is an email.");
        emailService.sendSimpleMessage(email);
        assertMessageReceived(email);
    }

    private void assertMessageReceived(final Email email) throws MessagingException, IOException {
        final MimeMessage[] receivedMessages = greenMail.getReceivedMessages();
        assertEquals(1, receivedMessages.length);
        final MimeMessage current = receivedMessages[0];
        assertEquals(email.getSubject(), current.getSubject());
        assertEquals(email.getTo(), current.getAllRecipients()[0].toString());
        assertTrue(String.valueOf(current.getContent()).contains(email.getContent()));
    }

}

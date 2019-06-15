/*
 * Copyright (C) 2011-2018 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchkatalog.app.email;

import wbh.bookworm.hoerbuchkatalog.domain.email.Email;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.UnsupportedEncodingException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;

/* TODO In .infrastructure/aoc.platform verschieben */
@Component
public final class EmailService {

    private static final Logger LOGGER = LoggerFactory.getLogger(EmailService.class);

    private final JavaMailSender javaMailSender;

    @Autowired
    public EmailService(final JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }

    void sendSimpleMessage(final Email email) {
        final SimpleMailMessage message = new SimpleMailMessage();
        message.setSubject(email.getSubject());
        message.setText(email.getContent());
        message.setTo(email.getTo());
        message.setFrom(email.getFrom());
        javaMailSender.send(message);
    }

    public void send(final String to, final String cc, final String subject, String text) {
        LOGGER.trace("Sende E-Mail an {}, Cc {}, Betreff {}", to, cc, subject);
        final MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        try {
            final MimeMessageHelper helper = new MimeMessageHelper(
                    mimeMessage, true, "UTF-8");
            /* TODO Konfiguration */helper.setFrom("wbh@wbh-online.de",
                    "Westdeutsche Bibliothek der Hörmedien für blinde, seh- und lesebehinderte Menschen e.V.");
            helper.setTo(to);
            helper.setCc(cc);
            helper.setSubject(subject);
            helper.setText(text, true);
            helper.setSentDate(datumZeitInZeitzoneBerlin());
            javaMailSender.send(mimeMessage);
            LOGGER.info("E-Mail an '{}', Cc '{}', Betreff '{}' gesendet", to, cc, subject);
        } catch (UnsupportedEncodingException | MessagingException e) {
            LOGGER.error("", e);
        }
    }

    private Date datumZeitInZeitzoneBerlin() {
        return Date.from(ZonedDateTime.now(ZoneId.of("Europe/Berlin")).toInstant());
    }

}

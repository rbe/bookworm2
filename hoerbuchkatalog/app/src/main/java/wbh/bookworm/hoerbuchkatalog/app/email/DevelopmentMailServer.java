/*
 * Copyright (C) 2011-2018 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchkatalog.app.email;

import com.icegreen.greenmail.user.UserException;
import com.icegreen.greenmail.util.GreenMail;
import com.icegreen.greenmail.util.ServerSetup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
@Profile("development")
public final class DevelopmentMailServer {

    private static final Logger LOGGER = LoggerFactory.getLogger(DevelopmentMailServer.class);

    @PostConstruct
    private void postConstruct() {
        final ServerSetup serverSetup = new ServerSetup(
                8025, "127.0.0.1", "smtp")
                .setVerbose(true);
        LOGGER.debug("Starte GreenMail server {}", serverSetup);
        final GreenMail greenMail = new GreenMail(serverSetup);
        try {
            greenMail.getManagers().getUserManager()
                    .createUser("user@example.com", "username", "password");
            greenMail.start();
        } catch (UserException e) {
            LOGGER.error("", e);
        }
    }

}

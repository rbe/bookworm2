/*
 * Copyright (C) 2011-2018 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchkatalog.assembly;

import com.icegreen.greenmail.junit5.GreenMailExtension;
import com.icegreen.greenmail.user.UserException;
import com.icegreen.greenmail.util.ServerSetup;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@Disabled("Mailserver steht nicht bereit")
class HoerbuchkatalogMainTest {

    private static ServerSetup serverSetup;

    @RegisterExtension
    static GreenMailExtension greenMail;

    static {
        serverSetup = new ServerSetup(8025, "127.0.0.1", "smtp")
                .setVerbose(true);
        greenMail = new GreenMailExtension(serverSetup);
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
    void shouldStartApp() {
        final ConfigurableApplicationContext applicationContext =
                SpringApplication.run(HoerbuchkatalogMain.class);
        assertTrue(applicationContext.isRunning());
    }

    @EnableAutoConfiguration
    @ComponentScan(basePackages = {
            "wbh.bookworm.hoerbuchkatalog"
    })
    public static class TestConfiguration {
    }

}

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

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(classes = {DlsBestellungTestAppConfig.class})
@SpringBootConfiguration
@ExtendWith(SpringExtension.class)
@Disabled
class SftpClientTest {

    @Autowired
    private SftpClient sftpClient;

    @Test
    @Disabled
    void shouldLogin() {
        sftpClient.with(delegate -> {
            assertEquals("/", delegate.pwd());
        });
    }

}

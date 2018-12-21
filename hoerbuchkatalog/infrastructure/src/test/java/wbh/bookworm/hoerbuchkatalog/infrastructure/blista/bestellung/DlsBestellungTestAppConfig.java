/*
 * Copyright (C) 2011-2018 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchkatalog.infrastructure.blista.bestellung;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/*
@SpringBootTest(classes = {
        DlsBestellung.class,
        RestServiceClient.class
})
@SpringBootConfiguration
@Import({DlsBestellungAppConfig.class})
*/
@Configuration
@Import({DlsBestellungAppConfig.class})
public class DlsBestellungTestAppConfig {
}

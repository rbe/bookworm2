/*
 * Copyright (C) 2011-2019 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchkatalog.repository.lieferung;

import wbh.bookworm.hoerbuchkatalog.infrastructure.blista.lieferung.DlsLieferung;
import wbh.bookworm.hoerbuchkatalog.infrastructure.blista.lieferung.DlsLieferungAppConfig;
import wbh.bookworm.hoerbuchkatalog.repository.katalog.Hoerbuchkatalog;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({DlsLieferungAppConfig.class})
@ComponentScan(basePackageClasses = {
        DownloadsRepository.class,
        Hoerbuchkatalog.class,
        DlsLieferung.class
})
public class LieferungAppConfig {
}

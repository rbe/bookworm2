/*
 * Copyright (C) 2011-2018 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchkatalog.repository.config;

import wbh.bookworm.hoerbuchkatalog.repository.bestellung.BestellungAppConfig;
import wbh.bookworm.hoerbuchkatalog.repository.lieferung.LieferungAppConfig;
import wbh.bookworm.hoerbuchkatalog.repository.email.EmailAppConfig;
import wbh.bookworm.hoerbuchkatalog.repository.katalog.KatalogAppConfig;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({
        KatalogAppConfig.class,
        BestellungAppConfig.class,
        EmailAppConfig.class,
        LieferungAppConfig.class
})
public class RepositoryConfig {
}

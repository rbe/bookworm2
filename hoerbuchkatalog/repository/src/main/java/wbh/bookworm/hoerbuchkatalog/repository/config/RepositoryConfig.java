/*
 * Copyright (C) 2011-2018 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchkatalog.repository.config;

import wbh.bookworm.hoerbuchkatalog.repository.bestellung.BestellungAppConfig;
import wbh.bookworm.hoerbuchkatalog.repository.downloads.RedisDownloadsAppConfig;
import wbh.bookworm.hoerbuchkatalog.repository.email.EmailAppConfig;
import wbh.bookworm.hoerbuchkatalog.repository.katalog.KatalogAppConfig;
import wbh.bookworm.hoerbuchkatalog.repository.lieferung.LieferungAppConfig;
import wbh.bookworm.hoerbuchkatalog.repository.nutzerdaten.NutzerdatenAppConfig;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
@Import({
        KatalogAppConfig.class,
        BestellungAppConfig.class,
        RedisDownloadsAppConfig.class,
        EmailAppConfig.class,
        LieferungAppConfig.class,
        NutzerdatenAppConfig.class
})
public class RepositoryConfig {

    private static final Logger LOGGER = LoggerFactory.getLogger(RepositoryConfig.class);

    @Bean
    @ConditionalOnMissingBean
    public ExecutorService executorService() {
        final ExecutorService executorService = Executors.newWorkStealingPool();
        LOGGER.debug("Created {}", executorService);
        return executorService;
    }

}

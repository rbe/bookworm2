/*
 * Copyright (C) 2011-2019 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchkatalog.repository.lieferung;

import wbh.bookworm.hoerbuchkatalog.infrastructure.blista.lieferung.DlsLieferung;
import wbh.bookworm.hoerbuchkatalog.infrastructure.blista.lieferung.DlsLieferungAppConfig;
import wbh.bookworm.hoerbuchkatalog.repository.config.RepositoryResolver;
import wbh.bookworm.hoerbuchkatalog.repository.katalog.Hoerbuchkatalog;

import aoc.mikrokosmos.io.fs.FilesystemWatcher;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
@Import({DlsLieferungAppConfig.class})
@ComponentScan(basePackageClasses = {
        FilesystemWatcher.class,
        RepositoryResolver.class,
        DownloadsRepository.class,
        CdLieferungRepository.class,
        Hoerbuchkatalog.class,
        DlsLieferung.class
})
@EnableConfigurationProperties
public class LieferungAppConfig {

    private static final Logger LOGGER = LoggerFactory.getLogger(LieferungAppConfig.class);

    @Bean
    @ConditionalOnMissingBean({ExecutorService.class})
    public ExecutorService executorService() {
        final ExecutorService executorService = Executors.newWorkStealingPool(
                Runtime.getRuntime().availableProcessors());
        LOGGER.debug("Created {}", executorService);
        return executorService;
    }

}

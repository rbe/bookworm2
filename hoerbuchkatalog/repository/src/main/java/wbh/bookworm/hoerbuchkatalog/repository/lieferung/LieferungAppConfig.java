/*
 * Copyright (C) 2011-2019 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchkatalog.repository.lieferung;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import wbh.bookworm.hoerbuchkatalog.repository.config.RepositoryResolver;
import wbh.bookworm.hoerbuchkatalog.repository.katalog.Hoerbuchkatalog;

import aoc.mikrokosmos.ddd.search.LuceneIndexConfig;
import aoc.mikrokosmos.ddd.search.LuceneIndexFactory;
import aoc.mikrokosmos.io.fs.FilesystemWatcher;

@Configuration
@ComponentScan(basePackageClasses = {
        FilesystemWatcher.class,
        RepositoryResolver.class,
        CdLieferungRepository.class,
        Hoerbuchkatalog.class,
        LuceneIndexFactory.class
})
@EnableConfigurationProperties(LuceneIndexConfig.class)
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

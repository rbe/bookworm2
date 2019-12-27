/*
 * Copyright (C) 2011-2019 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchkatalog.repository.nutzerdaten;

import aoc.mikrokosmos.io.fs.FilesystemWatcher;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@SpringBootApplication(scanBasePackageClasses = {
        FilesystemWatcher.class,
        HoererRepository.class
})
@SpringBootConfiguration
@EnableConfigurationProperties
public class NutzerdatenAppConfig {

    private static final Logger LOGGER = LoggerFactory.getLogger(NutzerdatenAppConfig.class);

    @Bean
    @ConditionalOnMissingBean({ExecutorService.class})
    public ExecutorService executorService() {
        final ExecutorService executorService = Executors.newWorkStealingPool(
                Runtime.getRuntime().availableProcessors());
        LOGGER.debug("Created {}", executorService);
        return executorService;
    }

}

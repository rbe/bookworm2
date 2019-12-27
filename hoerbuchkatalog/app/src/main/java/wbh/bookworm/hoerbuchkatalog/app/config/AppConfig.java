/*
 * Copyright (C) 2011-2018 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchkatalog.app.config;

import wbh.bookworm.hoerbuchkatalog.app.bestellung.BestellungService;
import wbh.bookworm.hoerbuchkatalog.app.email.EmailService;
import wbh.bookworm.hoerbuchkatalog.app.hoerer.HoererService;
import wbh.bookworm.hoerbuchkatalog.app.katalog.HoerbuchkatalogService;
import wbh.bookworm.hoerbuchkatalog.app.lieferung.DownloadsLieferungService;
import wbh.bookworm.hoerbuchkatalog.domain.config.DomainConfig;
import wbh.bookworm.hoerbuchkatalog.infrastructure.blista.config.InfrastructureBlistaConfig;
import wbh.bookworm.hoerbuchkatalog.repository.config.RepositoryConfig;
import wbh.bookworm.hoerbuchkatalog.repository.config.RepositoryResolver;
import wbh.bookworm.hoerbuchkatalog.repository.lieferung.LieferungAppConfig;

import aoc.mikrokosmos.io.fs.FilesystemWatcher;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.thymeleaf.TemplateEngine;

@Configuration
@Import({
        RepositoryConfig.class,
        DomainConfig.class,
        InfrastructureBlistaConfig.class
})
@ComponentScan(basePackageClasses = {
        FilesystemWatcher.class,
        RepositoryResolver.class,
        HoerbuchkatalogService.class,
        BestellungService.class,
        DownloadsLieferungService.class,
        HoererService.class,
        EmailService.class,
        TemplateEngine.class
})
@EnableAsync
@EnableScheduling
@EnableConfigurationProperties
public class AppConfig {

    private static final Logger LOGGER = LoggerFactory.getLogger(LieferungAppConfig.class);

    @Bean
    static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }

/*
    @Bean
    @Primary
    //@ConditionalOnMissingBean
    public ThreadPoolTaskExecutor threadPoolTaskExecutor() {
        final ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
        taskExecutor.setThreadNamePrefix("hoerbuchkatalogExecutor-");
        taskExecutor.setCorePoolSize(Runtime.getRuntime().availableProcessors());
        taskExecutor.setMaxPoolSize(Runtime.getRuntime().availableProcessors() * 10);
        taskExecutor.setQueueCapacity(0);
        taskExecutor.initialize();
        LOGGER.debug("Created {}", taskExecutor);
        return taskExecutor;
    }

    @Bean
    @Primary
    //@ConditionalOnMissingBean
    public ExecutorService executorService() {
        final ExecutorService executorService = Executors.newWorkStealingPool(
                Runtime.getRuntime().availableProcessors());
        LOGGER.debug("Created {}", executorService);
        return executorService;
    }
*/

}

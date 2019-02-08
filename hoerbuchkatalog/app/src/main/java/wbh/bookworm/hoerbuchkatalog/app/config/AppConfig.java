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
import wbh.bookworm.hoerbuchkatalog.repository.lieferung.LieferungAppConfig;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.thymeleaf.TemplateEngine;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
@Import({
        RepositoryConfig.class,
        DomainConfig.class,
        InfrastructureBlistaConfig.class
})
@ComponentScan(basePackageClasses = {
        HoerbuchkatalogService.class,
        BestellungService.class,
        DownloadsLieferungService.class,
        HoererService.class,
        EmailService.class,
        TemplateEngine.class
})
@EnableAsync
@EnableConfigurationProperties
public class AppConfig {

    private static final Logger LOGGER = LoggerFactory.getLogger(LieferungAppConfig.class);

    @Bean
    static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }

    // TODO ConcurrentTaskExecutor?
    @Bean
    @ConditionalOnMissingBean
    public TaskExecutor taskExecutor() {
        final ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
        taskExecutor.setCorePoolSize(5);
        taskExecutor.setMaxPoolSize(10);
        taskExecutor.setThreadNamePrefix("hoerbuchkatalogExecutor-");
        taskExecutor.initialize();
        return taskExecutor;
    }

    @Bean
    @ConditionalOnMissingBean
    public ExecutorService executorService() {
        final ExecutorService executorService = Executors.newWorkStealingPool(
                Runtime.getRuntime().availableProcessors() / 2);
        LOGGER.debug("Created {}", executorService);
        return executorService;
    }

}

/*
 * Copyright (C) 2011-2018 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchkatalog.app.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.thymeleaf.TemplateEngine;

import wbh.bookworm.hoerbuchkatalog.app.bestellung.BestellungService;
import wbh.bookworm.hoerbuchkatalog.app.email.EmailService;
import wbh.bookworm.hoerbuchkatalog.app.hoerer.HoererService;
import wbh.bookworm.hoerbuchkatalog.app.katalog.HoerbuchkatalogService;
import wbh.bookworm.hoerbuchkatalog.app.lieferung.DownloadsLieferungService;
import wbh.bookworm.hoerbuchkatalog.domain.config.DomainConfig;
import wbh.bookworm.hoerbuchkatalog.infrastructure.blista.config.InfrastructureBlistaConfig;
import wbh.bookworm.hoerbuchkatalog.repository.config.RepositoryConfig;
import wbh.bookworm.hoerbuchkatalog.repository.config.RepositoryResolver;

import aoc.mikrokosmos.io.fs.FilesystemWatcher;

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
@SuppressWarnings("java:S1118")
public class AppConfig {

    /*@Bean
    static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }*/

}

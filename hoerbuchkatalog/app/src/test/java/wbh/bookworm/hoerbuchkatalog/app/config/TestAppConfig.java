/*
 * Copyright (C) 2011-2018 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchkatalog.app.config;

import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.thymeleaf.TemplateEngine;

import wbh.bookworm.hoerbuchkatalog.app.bestellung.BestellungService;
import wbh.bookworm.hoerbuchkatalog.app.email.EmailService;
import wbh.bookworm.hoerbuchkatalog.app.katalog.HoerbuchkatalogService;
import wbh.bookworm.hoerbuchkatalog.app.lieferung.DownloadsLieferungService;
import wbh.bookworm.hoerbuchkatalog.domain.config.DomainConfig;
import wbh.bookworm.hoerbuchkatalog.infrastructure.blista.config.InfrastructureBlistaConfig;
import wbh.bookworm.hoerbuchkatalog.repository.config.RepositoryConfig;
import wbh.bookworm.hoerbuchkatalog.repository.katalog.HoerbuchkatalogConfig;

import aoc.mikrokosmos.ddd.search.LuceneIndexConfig;
import aoc.mikrokosmos.ddd.search.LuceneIndexFactory;

@SpringBootApplication
@SpringBootConfiguration
@EnableConfigurationProperties({LuceneIndexConfig.class, HoerbuchkatalogConfig.class})
@Import({
        RepositoryConfig.class,
        DomainConfig.class,
        InfrastructureBlistaConfig.class
})
@ComponentScan(basePackageClasses = {
        HoerbuchkatalogService.class,
        BestellungService.class,
        DownloadsLieferungService.class,
        EmailService.class,
        TemplateEngine.class,
        LuceneIndexFactory.class
})
@SuppressWarnings("java:S1118")
public class TestAppConfig {

    @Bean
    static PropertyPlaceholderConfigurer propertyPlaceholderConfigurer() {
        return new PropertyPlaceholderConfigurer();
    }

}

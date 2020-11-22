/*
 * Copyright (C) 2011-2018 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchkatalog.app.bestellung;

import java.nio.file.Path;

import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.thymeleaf.TemplateEngine;

import wbh.bookworm.hoerbuchkatalog.app.email.EmailService;
import wbh.bookworm.hoerbuchkatalog.app.katalog.HoerbuchkatalogService;
import wbh.bookworm.hoerbuchkatalog.domain.config.DomainConfig;
import wbh.bookworm.hoerbuchkatalog.infrastructure.blista.config.InfrastructureBlistaConfig;
import wbh.bookworm.hoerbuchkatalog.repository.bestellung.BestellungRepository;
import wbh.bookworm.hoerbuchkatalog.repository.config.RepositoryConfig;
import wbh.bookworm.hoerbuchkatalog.repository.email.EmailRepository;
import wbh.bookworm.hoerbuchkatalog.repository.email.EmailTemplateRepository;
import wbh.bookworm.hoerbuchkatalog.repository.katalog.HoerbuchkatalogConfig;

import aoc.mikrokosmos.ddd.search.LuceneIndexConfig;

@SpringBootApplication
@SpringBootConfiguration
@Import({
        RepositoryConfig.class,
        DomainConfig.class,
        InfrastructureBlistaConfig.class
})
@ComponentScan(basePackageClasses = {
        HoerbuchkatalogService.class,
        BestellungService.class,
        EmailService.class,
        TemplateEngine.class
})
@EnableConfigurationProperties({LuceneIndexConfig.class, HoerbuchkatalogConfig.class})
public class CdBestellungTestAppConfig {

    @Bean
    public BestellungRepository bestellungRepository() {
        return new BestellungRepository(Path.of("target/var"));
    }

    @Bean
    public EmailRepository emailRepository() {
        return new EmailRepository(Path.of("target/var"));
    }

    @Bean
    public EmailTemplateRepository emailTemplateRepository() {
        return new EmailTemplateRepository(Path.of("target/var"));
    }

}

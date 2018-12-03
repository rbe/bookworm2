/*
 * Copyright (C) 2011-2018 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchkatalog.app.config;

import wbh.bookworm.hoerbuchkatalog.app.bestellung.BestellungService;
import wbh.bookworm.hoerbuchkatalog.app.bestellung.DownloadsLieferungService;
import wbh.bookworm.hoerbuchkatalog.app.email.EmailService;
import wbh.bookworm.hoerbuchkatalog.app.katalog.HoerbuchkatalogService;
import wbh.bookworm.hoerbuchkatalog.domain.config.DomainConfig;
import wbh.bookworm.hoerbuchkatalog.infrastructure.blista.config.InfrastructureBlistaConfig;
import wbh.bookworm.hoerbuchkatalog.repository.bestellung.BestellungRepository;
import wbh.bookworm.hoerbuchkatalog.repository.bestellung.MerklisteRepository;
import wbh.bookworm.hoerbuchkatalog.repository.bestellung.WarenkorbRepository;
import wbh.bookworm.hoerbuchkatalog.repository.config.RepositoryConfig;
import wbh.bookworm.hoerbuchkatalog.repository.email.EmailTemplateRepository;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.thymeleaf.TemplateEngine;

import java.nio.file.Path;

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
        EmailService.class,
        TemplateEngine.class
})
@EnableConfigurationProperties
public class AppConfig {

    @Bean
    static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }

    @Bean
    public WarenkorbRepository warenkorbRepository() {
        return new WarenkorbRepository(Path.of("var"));
    }

    @Bean
    public MerklisteRepository merklisteRepository() {
        return new MerklisteRepository(Path.of("var"));
    }

    @Bean
    public BestellungRepository bestellungRepository() {
        return new BestellungRepository(Path.of("var"));
    }

    @Bean
    public EmailTemplateRepository emailTemplateRepository() {
        return new EmailTemplateRepository(Path.of("target/var"));
    }

}

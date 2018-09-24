/*
 * Copyright (C) 2011-2018 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchkatalog.app;

import wbh.bookworm.hoerbuchkatalog.app.bestellung.BestellungService;
import wbh.bookworm.hoerbuchkatalog.app.email.EmailService;
import wbh.bookworm.hoerbuchkatalog.app.katalog.HoerbuchkatalogService;
import wbh.bookworm.hoerbuchkatalog.domain.DomainConfig;
import wbh.bookworm.hoerbuchkatalog.repository.RepositoryConfig;

import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

@Configuration
@EnableConfigurationProperties
@ComponentScan(basePackageClasses = {
        HoerbuchkatalogService.class,
        BestellungService.class,
        EmailService.class,
        RepositoryConfig.class,
        DomainConfig.class
})
public class AppConfig {

    @Bean
    static PropertyPlaceholderConfigurer bookwormProperties() {
        final PropertyPlaceholderConfigurer propertyPlaceholderConfigurer = new PropertyPlaceholderConfigurer();
        propertyPlaceholderConfigurer.setLocations(new ClassPathResource("/conf/hoerbuchkatalog.properties"));
        return propertyPlaceholderConfigurer;
    }

    /*
    @Bean
    static YamlPropertySourceLoader bookwormYaml() throws IOException {
        YamlPropertySourceLoader yamlPropertySourceLoader = new YamlPropertySourceLoader();
        final List<PropertySource<?>> bookworm =
            yamlPropertySourceLoader.load("bookworm", new ClassPathResource("/conf/hoerbuchkatalog.yaml"));
        return yamlPropertySourceLoader;
    }
    */

}

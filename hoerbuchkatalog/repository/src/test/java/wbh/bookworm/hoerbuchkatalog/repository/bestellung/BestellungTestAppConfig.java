/*
 * Copyright (C) 2011-2018 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchkatalog.repository.bestellung;

import wbh.bookworm.hoerbuchkatalog.infrastructure.blista.bestellung.DlsBestellungAppConfig;

import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

import java.nio.file.Path;

@SpringBootApplication
@SpringBootConfiguration
@EnableConfigurationProperties
@Import({DlsBestellungAppConfig.class})
public class BestellungTestAppConfig {

    @Bean
    static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }

    @Bean
    public WarenkorbRepository warenkorbRepository() {
        return new WarenkorbRepository(Path.of("target/var"));
    }

    @Bean
    public MerklisteRepository merklisteRepository() {
        return new MerklisteRepository(Path.of("target/var"));
    }

    @Bean
    public BestellungRepository bestellungRepository() {
        return new BestellungRepository(Path.of("target/var"));
    }

}

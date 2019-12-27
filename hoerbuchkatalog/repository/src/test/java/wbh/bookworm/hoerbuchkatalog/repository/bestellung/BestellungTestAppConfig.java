/*
 * Copyright (C) 2011-2018 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchkatalog.repository.bestellung;

import wbh.bookworm.hoerbuchkatalog.infrastructure.blista.bestellung.DlsBestellung;
import wbh.bookworm.hoerbuchkatalog.infrastructure.blista.restdlskatalog.RestServiceClient;

import aoc.mikrokosmos.io.fs.FilesystemWatcher;

import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

import java.nio.file.Path;

@SpringBootApplication(scanBasePackageClasses = {
        FilesystemWatcher.class,
        WarenkorbRepository.class,
        DlsBestellung.class,
        RestServiceClient.class
})
@SpringBootConfiguration
@EnableConfigurationProperties
public class BestellungTestAppConfig {

    /* TODO Eigene Repositories für den Test
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
    */

}

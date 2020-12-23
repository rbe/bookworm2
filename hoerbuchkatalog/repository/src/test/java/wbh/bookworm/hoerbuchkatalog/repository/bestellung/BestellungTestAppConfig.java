/*
 * Copyright (C) 2011-2018 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchkatalog.repository.bestellung;

import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import aoc.mikrokosmos.io.fs.FilesystemWatcher;

@SpringBootApplication(scanBasePackageClasses = {
        FilesystemWatcher.class,
        WarenkorbRepository.class
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

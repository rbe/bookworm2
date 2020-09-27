/*
 * Copyright (C) 2011-2018 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchkatalog.repository.bestellung;

import java.nio.file.Path;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackageClasses = {
        BestellungRepository.class
})
public class BestellungAppConfig {

    @Bean
    public WarenkorbRepository warenkorbRepository(@Value("${repository.path}") final String repositoryPath) {
        return new WarenkorbRepository(Path.of(repositoryPath));
    }

    @Bean
    public MerklisteRepository merklisteRepository(@Value("${repository.path}") final String repositoryPath) {
        return new MerklisteRepository(Path.of(repositoryPath));
    }

    @Bean
    public BestellungRepository bestellungRepository(@Value("${repository.path}") final String repositoryPath) {
        return new BestellungRepository(Path.of(repositoryPath));
    }

}

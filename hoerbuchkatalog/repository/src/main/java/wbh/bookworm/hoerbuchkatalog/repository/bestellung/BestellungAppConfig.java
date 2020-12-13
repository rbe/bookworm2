/*
 * Copyright (C) 2011-2018 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchkatalog.repository.bestellung;

import java.nio.file.Path;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

@Configuration
@ComponentScan(basePackageClasses = {
        BestellungRepository.class
})
public class BestellungAppConfig {

    private final String repositoryPath;

    public BestellungAppConfig(final Environment environment) {
        repositoryPath = environment.getProperty("repository.path");
    }

    @Bean
    public WarenkorbRepository warenkorbRepository() {
        return new WarenkorbRepository(Path.of(repositoryPath));
    }

    @Bean
    public MerklisteRepository merklisteRepository() {
        return new MerklisteRepository(Path.of(repositoryPath));
    }

    @Bean
    public BestellungRepository bestellungRepository() {
        return new BestellungRepository(Path.of(repositoryPath));
    }

    @Bean
    public SessionRepository sessionRepository() {
        return new SessionRepository(Path.of(repositoryPath));
    }

}

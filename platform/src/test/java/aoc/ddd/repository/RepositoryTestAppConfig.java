/*
 * Copyright (C) 2011-2018 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package aoc.ddd.repository;

import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

import java.nio.file.Path;

@SpringBootApplication(scanBasePackageClasses = {
        DomainRepository.class
})
@SpringBootConfiguration
@EnableConfigurationProperties
public class RepositoryTestAppConfig {

    @Bean
    public AnAggregateRepository anAggregateRepository() {
        return new AnAggregateRepository(Path.of("target/var"));
    }

}

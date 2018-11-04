/*
 * Copyright (C) 2011-2018 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchkatalog.app.email;

import wbh.bookworm.hoerbuchkatalog.domain.config.DomainConfig;
import wbh.bookworm.hoerbuchkatalog.domain.email.EmailRepository;

import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import java.nio.file.Path;

@SpringBootApplication
@SpringBootConfiguration
@Configuration
@EnableConfigurationProperties
@ComponentScan(basePackageClasses = {
        EmailService.class,
        EmailRepository.class,
        DomainConfig.class
})
public class EmailTestAppConfig {

    @Bean
    public EmailRepository emailRepository() {
        return new EmailRepository(Path.of("target"));
    }

}

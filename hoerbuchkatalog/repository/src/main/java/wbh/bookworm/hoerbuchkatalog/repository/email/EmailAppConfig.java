/*
 * Copyright (C) 2011-2018 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchkatalog.repository.email;

import java.nio.file.Path;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

@Configuration
@ComponentScan(basePackageClasses = {
        EmailRepository.class
})
public class EmailAppConfig {

    private final String repositoryPath;

    public EmailAppConfig(final Environment environment) {
        repositoryPath = environment.getProperty("repository.path");
    }

    @Bean
    public EmailTemplateRepository emailTemplateRepository() {
        return new EmailTemplateRepository(Path.of(repositoryPath));
    }

}

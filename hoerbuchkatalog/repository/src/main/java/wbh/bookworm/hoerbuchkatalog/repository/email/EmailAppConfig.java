/*
 * Copyright (C) 2011-2018 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchkatalog.repository.email;

import java.nio.file.Path;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackageClasses = {
        EmailRepository.class
})
public class EmailAppConfig {

    @Bean
    public EmailTemplateRepository emailTemplateRepository(@Value("${repository.path}") final String repositoryPath) {
        return new EmailTemplateRepository(Path.of(repositoryPath));
    }

}

/*
 * Copyright (C) 2011-2018 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.platform.repository.ddd;

import wbh.bookworm.platform.ddd.repository.DomainRepository;

import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication(scanBasePackageClasses = {
        DomainRepository.class
})
@SpringBootConfiguration
@EnableConfigurationProperties
public class TestAppConfig {
}

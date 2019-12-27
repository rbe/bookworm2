/*
 * Copyright (C) 2011-2018 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchkatalog.repository.katalog;

import aoc.mikrokosmos.io.fs.FilesystemWatcher;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication(scanBasePackageClasses = {
        FilesystemWatcher.class,
        Hoerbuchkatalog.class
})
@SpringBootConfiguration
@EnableConfigurationProperties
public class KatalogTestAppConfig {

    private static final Logger LOGGER = LoggerFactory.getLogger(KatalogTestAppConfig.class);

}

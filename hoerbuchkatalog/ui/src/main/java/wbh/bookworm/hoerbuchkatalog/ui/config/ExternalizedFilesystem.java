/*
 * Copyright (C) 2011-2018 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchkatalog.ui.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;

final class ExternalizedFilesystem {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExternalizedFilesystem.class);

    private static final String TEMPLATE_PATH_SYSTEM_VARIABLE = "HOERBUCHKATALOG_TEMPLATE";

    static final Path TEMPLATES_PATH;

    static {
        final String templatePath = System.getenv(TEMPLATE_PATH_SYSTEM_VARIABLE);
        if (null == templatePath || templatePath.isBlank()) {
            TEMPLATES_PATH = Path.of(".");
            LOGGER.warn("Environment variable '{}' not set, using '{}'",
                    TEMPLATE_PATH_SYSTEM_VARIABLE, TEMPLATES_PATH.toAbsolutePath());
        } else {
            TEMPLATES_PATH = Path.of(templatePath);
            LOGGER.info("Using '{}' as template path", TEMPLATES_PATH.toAbsolutePath());
        }
    }

    private ExternalizedFilesystem() {
        throw new AssertionError();
    }

}

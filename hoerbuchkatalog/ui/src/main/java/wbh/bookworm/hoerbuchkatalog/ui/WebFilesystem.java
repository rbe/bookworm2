/*
 * Copyright (C) 2011-2018 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchkatalog.ui;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

final class WebFilesystem {

    private static final Logger LOGGER = LoggerFactory.getLogger(WebFilesystem.class);

    static final String TEMPLATE_SYSTEM_VARIABLE = "HOERBUCHKATALOG_TEMPLATE";

    static final String BASE_PATH;

    static {
        BASE_PATH = System.getenv(TEMPLATE_SYSTEM_VARIABLE);
        if (null == WebFilesystem.BASE_PATH) {
            LOGGER.error("Variable {} not set!", TEMPLATE_SYSTEM_VARIABLE);
        }
    }

    private WebFilesystem() {
        throw new AssertionError();
    }

}

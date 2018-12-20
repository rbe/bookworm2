/*
 * Copyright (C) 2011-2018 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchkatalog.infrastructure.blista.restdlskatalog;

public final class RestServiceHttpConfig {

    public static final int CONNECT_TIMEOUT = 3 * 1_000;

    public static final int READ_TIMEOUT = 5 * 1_000;

    private RestServiceHttpConfig() {
        throw new AssertionError();
    }

}

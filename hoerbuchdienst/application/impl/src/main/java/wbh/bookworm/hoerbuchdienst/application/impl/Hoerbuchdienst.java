/*
 * Copyright (C) 2011-2020 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchdienst.application.impl;

import io.micronaut.runtime.Micronaut;

public class Hoerbuchdienst {

    public static void main(final String[] args) {
        // TODO Setup UncaughtExceptionHandler and cleanup temporary files?
        Micronaut.run(Hoerbuchdienst.class);
    }

}

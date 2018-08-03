/*
 * eu.artofcoding.bookworm
 *
 * Copyright (C) 2011-2017 art of coding UG, http://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchkatalog.domain;

import java.util.Objects;

/**
 * Value Object
 */
public final class Titelnummer extends DddValueObject<Titelnummer, String> {

    private static final long serialVersionUID = -1L;

    public Titelnummer(final String value) {
        Objects.requireNonNull(value);
        this.value = value;
    }

    public Titelnummer(final Titelnummer titelnummer) {
        Objects.requireNonNull(titelnummer);
        this.value = titelnummer.value;
    }

}

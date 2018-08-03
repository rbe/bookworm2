/*
 * eu.artofcoding.bookworm
 *
 * Copyright (C) 2011-2017 art of coding UG, http://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchkatalog.domain;

/**
 * Value Object
 */
public final class Hoerernummer extends DddValueObject<Hoerernummer, String> {

    private static final long serialVersionUID = -1L;

    public Hoerernummer(final String value) {
        checkValue(value);
        this.value = value;
    }

    public Hoerernummer(final Hoerernummer hoerernummer) {
        this(hoerernummer.value);
    }

    @Override
    boolean checkValue(final String value) {
        super.checkValue(value);
        final boolean hasValue = !value.trim().isEmpty();
        final boolean valueInConstraint = value.length() > 1 && value.length() < 10;
        return hasValue && valueInConstraint;
    }

}

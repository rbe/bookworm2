/*
 * eu.artofcoding.bookworm
 *
 * Copyright (C) 2011-2017 art of coding UG, http://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchkatalog.domain;

import java.util.regex.Pattern;

/**
 * Value Object
 */
public final class AghNummer extends DddValueObject<AghNummer, String> {

    private static final long serialVersionUID = -1L;

    private final Pattern pattern1 = Pattern.compile("\\d-\\d{7}-\\d-(\\d|x|X)");

    public AghNummer(final String value) {
        if (!checkValue(value)) {
            throw new IllegalArgumentException(String.format("'%s'", value));
        }
        this.value = value;
    }

    public AghNummer(final AghNummer aghNummer) {
        this(aghNummer.value);
    }

    @Override
    public boolean checkValue(String value) {
        super.checkValue(value);
        return pattern1.matcher(value).matches();
    }

}

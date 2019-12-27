/*
 * Copyright (C) 2011-2018 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchkatalog.domain.katalog;

import aoc.mikrokosmos.ddd.model.DomainSingleValueObject;

import java.util.regex.Pattern;

/**
 * Value Object
 */
public final class AghNummer extends DomainSingleValueObject<AghNummer, String> {

    private static final long serialVersionUID = -1L;

    private final Pattern pattern1 = Pattern.compile("\\d-\\d{7}-\\d-(\\d|x|X)");

    public AghNummer(final String value) {
        // TODO Does not work with checkValue(): super(value);
        if (!checkValue(value)) {
            throw new IllegalArgumentException(String.format("'%s'", value));
        }
        this.value = value;
    }

    public AghNummer(final AghNummer aghNummer) {
        this(aghNummer.value);
    }

    @Override
    public boolean checkValue(final String value) {
        super.checkValue(value);
        return pattern1.matcher(value).matches();
    }

}

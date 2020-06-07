/*
 * Copyright (C) 2011-2018 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.shared.domain.hoerbuch;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import aoc.mikrokosmos.ddd.model.DomainId;

/**
 * DomainId / Value Object
 */
public final class Titelnummer extends DomainId<String> {

    private static final long serialVersionUID = -1L;

    private static final String KAPITEL = "Kapitel";

    @JsonCreator
    public Titelnummer(@JsonProperty("value") final String value) {
        super(value);
    }

    public Titelnummer(final Integer value) {
        super(value);
    }

    public Titelnummer(final Titelnummer titelnummer) {
        this(titelnummer.value);
    }

    public static Titelnummer of(final String wbhKapitel) {
        if (wbhKapitel.endsWith(KAPITEL)) {
            int idx = wbhKapitel.indexOf(KAPITEL);
            return new Titelnummer(wbhKapitel.substring(0, idx));
        } else {
            return new Titelnummer(wbhKapitel);
        }
    }

    @Override
    public boolean checkValue(final String value) {
        return !value.isBlank() && value.trim().chars().anyMatch(i -> i >= 48 && i <= 57);
    }

}

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

    @JsonCreator
    public Titelnummer(final @JsonProperty("value") String value) {
        super(value);
    }

    public Titelnummer(final Integer value) {
        super(value);
    }

    public Titelnummer(final Titelnummer titelnummer) {
        this(titelnummer.value);
    }

    @Override
    public boolean checkValue(final String value) {
        return !value.isBlank() && value.trim().chars().anyMatch(i -> i >= 48 && i <= 57);
    }

}

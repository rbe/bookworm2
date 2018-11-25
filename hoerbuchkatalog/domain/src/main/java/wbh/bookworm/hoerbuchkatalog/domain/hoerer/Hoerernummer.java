/*
 * Copyright (C) 2011-2018 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchkatalog.domain.hoerer;

import aoc.ddd.model.DomainId;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * DomainId
 * (Spring AOP) Class cannot be final.
 */
public class Hoerernummer extends DomainId<String> {

    private static final long serialVersionUID = -1L;

    public static final Hoerernummer UNBEKANNT = new Hoerernummer("00000");

    @JsonIgnore
    public boolean isUnbekannt() {
        return this == UNBEKANNT;
    }

    @JsonIgnore
    public boolean isBekannt() {
        return this != UNBEKANNT;
    }

    @JsonCreator
    public Hoerernummer(final @JsonProperty("value") String value) {
        super(value);
    }

    public Hoerernummer(final Hoerernummer hoerernummer) {
        this(hoerernummer.value);
    }

    @Override
    public boolean checkValue(final String value) {
        super.checkValue(value);
        final boolean hasValue = !value.trim().isEmpty();
        final boolean valueInConstraint = value.length() > 1 && value.length() < 10;
        return hasValue && valueInConstraint;
    }

}

/*
 * Copyright (C) 2011-2018 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.shared.domain.hoerer;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import aoc.mikrokosmos.ddd.model.DomainId;

public final class Hoerernummer extends DomainId<String> {

    private static final long serialVersionUID = -1L;

    public static final Hoerernummer UNBEKANNT = new Hoerernummer("00000");

    @JsonIgnore
    public boolean isUnbekannt() {
        return this.equals(UNBEKANNT);
    }

    @JsonIgnore
    public boolean isBekannt() {
        return !this.equals(UNBEKANNT);
    }

    @JsonCreator
    public Hoerernummer(final @JsonProperty("value") String value) {
        super(value);
    }

    public Hoerernummer(final Integer hoerernummer) {
        this(String.valueOf(hoerernummer));
    }

    public Hoerernummer(final Hoerernummer hoerernummer) {
        this(hoerernummer.value);
    }

    @Override
    public boolean checkValue(final String value) {
        super.checkValue(value);
        final boolean hasValue = null != value && !value.isBlank();
        final boolean valueInConstraint = hasValue && value.length() > 0 && value.length() < 7;
        return hasValue && valueInConstraint;
    }

}

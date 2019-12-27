/*
 * Copyright (C) 2011-2018 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchkatalog.domain.hoerer;

import aoc.mikrokosmos.ddd.model.DomainSingleValueObject;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public final class Vorname extends DomainSingleValueObject<Vorname, String> {

    public static final Vorname UNBEKANNT = new Vorname("");

    @JsonCreator
    public Vorname(final @JsonProperty("value") String vorname) {
        super(vorname);
    }

    // TODO checkValue .trim() !.isBlank() !.isEmpty()

    @Override
    public boolean hasValue() {
        return !value.isBlank();
    }

}

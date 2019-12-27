/*
 * Copyright (C) 2011-2018 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchkatalog.domain.hoerer;

import aoc.mikrokosmos.ddd.model.DomainSingleValueObject;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public final class HoererEmail extends DomainSingleValueObject<HoererEmail, String> {

    public static final HoererEmail UNBEKANNT = new HoererEmail("");

    @JsonCreator
    public HoererEmail(final @JsonProperty("value") String hoereremail) {
        super(hoereremail);
    }

}

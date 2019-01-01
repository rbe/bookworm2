/*
 * Copyright (C) 2011-2018 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchkatalog.domain.hoerer;

import aoc.ddd.model.DomainSingleValueObject;

public final class HoererEmail extends DomainSingleValueObject<HoererEmail, String> {

    public static final HoererEmail UNBEKANNT = new HoererEmail("");

    public HoererEmail(final String hoereremail) {
        super(hoereremail);
    }

}

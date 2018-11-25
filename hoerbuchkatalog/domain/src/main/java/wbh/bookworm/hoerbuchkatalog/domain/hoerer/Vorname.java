/*
 * Copyright (C) 2011-2018 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchkatalog.domain.hoerer;

import aoc.ddd.model.DomainSingleValueObject;

public final class Vorname extends DomainSingleValueObject<Vorname, String> {

    public Vorname(final String vorname) {
        super(vorname);
    }

}

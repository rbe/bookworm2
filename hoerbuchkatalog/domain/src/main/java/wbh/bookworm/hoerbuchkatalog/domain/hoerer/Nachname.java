/*
 * Copyright (C) 2011-2018 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchkatalog.domain.hoerer;

import aoc.ddd.model.DomainSingleValueObject;

public final class Nachname extends DomainSingleValueObject<Nachname, String> {

    public static final Nachname UNBEKANNT = new Nachname("");

    public Nachname(final String nachname) {
        super(nachname);
    }

    @Override
    public boolean hasValue() {
        return !value.isBlank();
    }

}

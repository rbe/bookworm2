/*
 * Copyright (C) 2011-2018 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchkatalog.domain.bestellung;

import aoc.mikrokosmos.ddd.model.DomainSingleValueObject;
import aoc.mikrokosmos.lang.strings.RandomStringGenerator;

public final class BestellungSessionId extends DomainSingleValueObject<String, String> {

    public BestellungSessionId() {
        super(RandomStringGenerator.next());
    }

    public BestellungSessionId(final String id) {
        super(id);
    }

    public static BestellungSessionId of(final String id) {
        return new BestellungSessionId(id);
    }

    @Override
    public boolean checkValue(final String s) {
        return s.length() >= 24;
    }

}

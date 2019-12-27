/*
 * Copyright (C) 2011-2018 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchkatalog.app.bestellung;

import aoc.mikrokosmos.ddd.model.DomainSingleValueObject;
import aoc.mikrokosmos.lang.strings.RandomStringGenerator;

public final class BestellungSessionId extends DomainSingleValueObject<String, String> {

    BestellungSessionId() {
        super(RandomStringGenerator.next());
    }

}

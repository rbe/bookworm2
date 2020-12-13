/*
 * Copyright (C) 2011-2018 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchkatalog.domain.bestellung;

import aoc.mikrokosmos.ddd.model.DomainSingleValueObject;
import aoc.mikrokosmos.lang.strings.RandomStringGenerator;

public final class BestellungSessionId extends DomainSingleValueObject<String, String> {

    private static final BestellungSessionId UNBEKANNT = new BestellungSessionId("UNBEKANNT");

    public BestellungSessionId() {
        super(RandomStringGenerator.next());
    }

    public BestellungSessionId(final String id) {
        super(id);
    }

    public static BestellungSessionId unbekannt() {
        return UNBEKANNT;
    }

    public static BestellungSessionId of(final String id) {
        return null == id || id.isBlank() ? UNBEKANNT : new BestellungSessionId(id);
    }

    public boolean isBekannt() {
        return this != UNBEKANNT;
    }

    public boolean isUnbekannt() {
        return this == UNBEKANNT;
    }

}

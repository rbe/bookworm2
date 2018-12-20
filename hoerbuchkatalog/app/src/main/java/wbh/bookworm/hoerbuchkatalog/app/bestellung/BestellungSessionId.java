/*
 * Copyright (C) 2011-2018 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchkatalog.app.bestellung;

import aoc.ddd.model.DomainValueObject;

import java.util.Objects;

public final class BestellungSessionId extends DomainValueObject {

    private static final long serialVersionUID = 1L;

    private final String hoerernummer;

    private final String sessionId;

    public BestellungSessionId(final String hoerernummer, final String sessionId) {
        Objects.requireNonNull(hoerernummer);
        this.hoerernummer = hoerernummer;
        Objects.requireNonNull(sessionId);
        this.sessionId = sessionId;
    }

    public String getHoerernummer() {
        return hoerernummer;
    }

    public String getSessionId() {
        return sessionId;
    }

}

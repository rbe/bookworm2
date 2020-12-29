/*
 * Copyright (C) 2011-2018 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchkatalog.domain.bestellung;

import wbh.bookworm.shared.domain.Hoerernummer;
import wbh.bookworm.shared.domain.Titelnummer;

import aoc.mikrokosmos.ddd.event.DomainAggregateWriteEvent;

public final class HoerbuchAufDieMerklisteGesetzt
        extends DomainAggregateWriteEvent<Merkliste, MerklisteId> {

    private static final long serialVersionUID = 1L;

    private final Hoerernummer hoerernummer;

    private final Titelnummer titelnummer;

    HoerbuchAufDieMerklisteGesetzt(final Hoerernummer hoerernummer, final Merkliste merkliste,
                                   final Titelnummer titelnummer) {
        super(merkliste);
        this.hoerernummer = hoerernummer;
        this.titelnummer = titelnummer;
    }

    public Hoerernummer getHoerernummer() {
        return hoerernummer;
    }

    public Titelnummer getTitelnummer() {
        return titelnummer;
    }

}

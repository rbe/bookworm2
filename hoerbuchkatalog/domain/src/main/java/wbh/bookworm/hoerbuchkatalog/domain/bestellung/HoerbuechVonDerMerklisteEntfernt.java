/*
 * Copyright (C) 2011-2018 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchkatalog.domain.bestellung;

import wbh.bookworm.hoerbuchkatalog.domain.hoerer.Hoerernummer;
import wbh.bookworm.hoerbuchkatalog.domain.katalog.Titelnummer;

import aoc.mikrokosmos.ddd.event.DomainAggregateWriteEvent;

public final class HoerbuechVonDerMerklisteEntfernt
        extends DomainAggregateWriteEvent<Merkliste, MerklisteId> {

    private final Hoerernummer hoerernummer;

    private final Titelnummer titelnummer;

    HoerbuechVonDerMerklisteEntfernt(final Hoerernummer hoerernummer, final Merkliste merkliste,
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

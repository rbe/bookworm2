/*
 * Copyright (C) 2011-2018 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchkatalog.domain.bestellung;

import wbh.bookworm.hoerbuchkatalog.domain.hoerer.Hoerernummer;
import wbh.bookworm.hoerbuchkatalog.domain.katalog.Titelnummer;

import aoc.mikrokosmos.ddd.event.DomainEvent;

import java.util.Set;

/**
 * Event
 */
public abstract class TitelnummernBestellt extends DomainEvent {

    private static final long serialVersionUID = -1L;

    protected final Hoerernummer hoerernummer;

    protected final transient Set<Titelnummer> titelnummern;

    TitelnummernBestellt(final Hoerernummer hoerernummer,
                         final Set<Titelnummer> titelnummern) {
        this.hoerernummer = hoerernummer;
        this.titelnummern = titelnummern;
    }

    public Hoerernummer getHoerernummer() {
        return hoerernummer;
    }

    public Set<Titelnummer> getTitelnummern() {
        return titelnummern;
    }

}

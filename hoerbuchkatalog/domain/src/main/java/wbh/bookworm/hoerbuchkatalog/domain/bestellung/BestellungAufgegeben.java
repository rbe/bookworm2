/*
 * Copyright (C) 2011-2018 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchkatalog.domain.bestellung;

import java.util.Objects;

import wbh.bookworm.shared.domain.Hoerernummer;

import aoc.mikrokosmos.ddd.event.DomainAggregateWriteEvent;

/**
 * Event
 */
public class BestellungAufgegeben extends DomainAggregateWriteEvent<Bestellung, BestellungId> {

    private static final long serialVersionUID = -1L;

    private final Hoerernummer hoerernummer;

    public BestellungAufgegeben(final Hoerernummer hoerernummer, final Bestellung bestellung) {
        super(bestellung);
        Objects.requireNonNull(hoerernummer);
        this.hoerernummer = hoerernummer;
    }

    public Hoerernummer getHoerernummer() {
        return hoerernummer;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        final BestellungAufgegeben that = (BestellungAufgegeben) o;
        return hoerernummer.equals(that.hoerernummer);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), hoerernummer);
    }

}

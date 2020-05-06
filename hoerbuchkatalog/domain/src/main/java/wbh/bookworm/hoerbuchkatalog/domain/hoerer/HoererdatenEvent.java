/*
 * Copyright (C) 2011-2019 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchkatalog.domain.hoerer;

import java.util.Objects;

import wbh.bookworm.shared.domain.hoerer.Hoerernummer;

import aoc.mikrokosmos.ddd.event.DomainEvent;

public abstract class HoererdatenEvent extends DomainEvent {

    private static final long serialVersionUID = -1L;

    protected final Hoerernummer hoerernummer;

    public HoererdatenEvent(final Hoerernummer hoerernummer) {
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
        final HoererdatenEvent that = (HoererdatenEvent) o;
        return hoerernummer.equals(that.hoerernummer);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), hoerernummer);
    }

}

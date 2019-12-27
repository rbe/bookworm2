/*
 * Copyright (C) 2011-2018 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchkatalog.domain.bestellung;

import wbh.bookworm.hoerbuchkatalog.domain.hoerer.Hoerernummer;

import aoc.mikrokosmos.ddd.event.DomainAggregateWriteEvent;

import java.util.Objects;

public class WarenkorbGeleert extends DomainAggregateWriteEvent<Warenkorb, WarenkorbId> {

    private final Hoerernummer hoerernummer;

    public WarenkorbGeleert(final Hoerernummer hoerernummer, final Warenkorb warenkorb) {
        super(warenkorb);
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
        final WarenkorbGeleert that = (WarenkorbGeleert) o;
        return Objects.equals(hoerernummer, that.hoerernummer);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), hoerernummer);
    }

}

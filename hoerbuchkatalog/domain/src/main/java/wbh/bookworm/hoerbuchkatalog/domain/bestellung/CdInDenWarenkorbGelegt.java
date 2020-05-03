/*
 * Copyright (C) 2011-2018 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchkatalog.domain.bestellung;

import wbh.bookworm.shared.domain.Hoerernummer;
import wbh.bookworm.shared.domain.Titelnummer;

import aoc.mikrokosmos.ddd.event.DomainAggregateWriteEvent;

public final class CdInDenWarenkorbGelegt extends DomainAggregateWriteEvent<Warenkorb, WarenkorbId> {

    private final Hoerernummer hoerernummer;

    private final Titelnummer titelnummer;

    CdInDenWarenkorbGelegt(final Hoerernummer hoerernummer, final CdWarenkorb cdWarenkorb,
                           final Titelnummer titelnummer) {
        super(cdWarenkorb);
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

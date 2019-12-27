/*
 * Copyright (C) 2011-2018 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchkatalog.domain.bestellung;

import wbh.bookworm.hoerbuchkatalog.domain.hoerer.Hoerernummer;
import wbh.bookworm.hoerbuchkatalog.domain.katalog.Titelnummer;

import aoc.mikrokosmos.ddd.event.DomainAggregateWriteEvent;

public final class DownloadAusDemWarenkorbEntfernt extends DomainAggregateWriteEvent<Warenkorb, WarenkorbId> {

    private static final long serialVersionUID = -1L;

    private final Hoerernummer hoerernummer;

    private final Titelnummer titelnummer;

    DownloadAusDemWarenkorbEntfernt(final Hoerernummer hoerernummer, final DownloadWarenkorb downloadWarenkorb,
                                    final Titelnummer titelnummer) {
        super(downloadWarenkorb);
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

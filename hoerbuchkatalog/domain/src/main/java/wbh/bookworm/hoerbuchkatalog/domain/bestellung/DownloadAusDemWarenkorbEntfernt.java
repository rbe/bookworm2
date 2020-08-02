/*
 * Copyright (C) 2011-2018 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchkatalog.domain.bestellung;

import wbh.bookworm.shared.domain.hoerbuch.Titelnummer;
import wbh.bookworm.shared.domain.mandant.Hoerernummer;

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

/*
 * Copyright (C) 2011-2018 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchkatalog.domain.bestellung;

import wbh.bookworm.hoerbuchkatalog.domain.hoerer.Hoerernummer;
import wbh.bookworm.hoerbuchkatalog.domain.katalog.Titelnummer;
import wbh.bookworm.platform.ddd.event.DomainAggregateWriteEvent;

public final class CdAusDemWarenkorbEntfernt extends DomainAggregateWriteEvent<Warenkorb, WarenkorbId> {

    private final Hoerernummer hoerernummer;

    private final Titelnummer titelnummer;

    CdAusDemWarenkorbEntfernt(final Hoerernummer hoerernummer, final CdWarenkorb cdWarenkorb,
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

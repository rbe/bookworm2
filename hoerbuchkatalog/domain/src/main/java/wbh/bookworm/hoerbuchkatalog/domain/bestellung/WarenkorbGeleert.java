/*
 * Copyright (C) 2011-2018 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchkatalog.domain.bestellung;

import wbh.bookworm.hoerbuchkatalog.domain.hoerer.Hoerernummer;
import wbh.bookworm.platform.ddd.event.DomainAggregateWriteEvent;

public class WarenkorbGeleert extends DomainAggregateWriteEvent<Warenkorb, WarenkorbId> {

    private final Hoerernummer hoerernummer;

    public WarenkorbGeleert(final Hoerernummer hoerernummer, final Warenkorb warenkorb) {
        super(warenkorb);
        this.hoerernummer = hoerernummer;
    }

    public Hoerernummer getHoerernummer() {
        return hoerernummer;
    }

}

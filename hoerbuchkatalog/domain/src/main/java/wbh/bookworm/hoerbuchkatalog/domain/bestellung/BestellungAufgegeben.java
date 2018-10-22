/*
 * Copyright (C) 2011-2018 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchkatalog.domain.bestellung;

import wbh.bookworm.hoerbuchkatalog.domain.hoerer.Hoerernummer;
import wbh.bookworm.platform.ddd.event.DomainAggregateWriteEvent;

/**
 * Event
 */
public class BestellungAufgegeben extends DomainAggregateWriteEvent<Bestellung, BestellungId> {

    private static final long serialVersionUID = -1L;

    private final Hoerernummer hoerernummer;

    public BestellungAufgegeben(final Hoerernummer hoerernummer, final Bestellung bestellung) {
        super(bestellung);
        this.hoerernummer = hoerernummer;
    }

    public Hoerernummer getHoerernummer() {
        return hoerernummer;
    }

}

/*
 * Copyright (C) 2011-2018 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchkatalog.domain.bestellung;

import wbh.bookworm.hoerbuchkatalog.domain.hoerer.Hoerernummer;
import wbh.bookworm.hoerbuchkatalog.domain.katalog.Titelnummer;
import wbh.bookworm.platform.ddd.event.DomainEvent;

import java.util.Set;

/**
 * Event
 */
public class BestellungAbgeschickt extends DomainEvent {

    private static final long serialVersionUID = -1L;

    private final Hoerernummer hoerernummer;

    private final Set<Titelnummer> cdTitelnummern;

    private final Set<Titelnummer> downloadTitelnummern;

    public BestellungAbgeschickt(final Hoerernummer hoerernummer,
                                 final Set<Titelnummer> cdTitelnummern,
                                 final Set<Titelnummer> downloadTitelnummern) {
        this.hoerernummer = hoerernummer;
        this.cdTitelnummern = cdTitelnummern;
        this.downloadTitelnummern = downloadTitelnummern;
    }

    public Hoerernummer getHoerernummer() {
        return hoerernummer;
    }

    public Set<Titelnummer> getCdTitelnummern() {
        return cdTitelnummern;
    }

    public Set<Titelnummer> getDownloadTitelnummern() {
        return downloadTitelnummern;
    }

}

/*
 * Copyright (C) 2011-2018 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchkatalog.domain.bestellung;

import java.util.Set;

import wbh.bookworm.shared.domain.hoerbuch.AghNummer;
import wbh.bookworm.shared.domain.mandant.Hoerernummer;

import aoc.mikrokosmos.ddd.event.DomainEvent;

/**
 * Event
 */
public final class HoerbuecherAlsDownloadBestellt extends DomainEvent {

    private static final long serialVersionUID = -1L;

    private final Hoerernummer hoerernummer;

    private final transient Set<AghNummer> aghNummern;

    public HoerbuecherAlsDownloadBestellt(final Hoerernummer hoerernummer,
                                          final Set<AghNummer> aghNummern) {
        this.hoerernummer = hoerernummer;
        this.aghNummern = aghNummern;
    }

    public Hoerernummer getHoerernummer() {
        return hoerernummer;
    }

    public Set<AghNummer> getAghNummer() {
        return aghNummern;
    }

}

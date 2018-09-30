/*
 * Copyright (C) 2011-2018 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchkatalog.domain.bestellung;

import wbh.bookworm.hoerbuchkatalog.domain.hoerer.Hoerernummer;
import wbh.bookworm.platform.ddd.event.DomainEvent;

/**
 * Event
 */
public class BestellungAbgeschickt extends DomainEvent {

    private static final long serialVersionUID = -1L;

    private final BestellungId bestellungId;

    private final Hoerernummer hoerernummer;

    private final WarenkorbId cdWarenkorbId;

    private final WarenkorbId downloadWarenkorbId;

    public BestellungAbgeschickt(final BestellungId bestellungId, final Hoerernummer hoerernummer,
                                 final WarenkorbId cdWarenkorbId,
                                 final WarenkorbId downloadWarenkorbId) {
        this.bestellungId = bestellungId;
        this.hoerernummer = hoerernummer;
        this.cdWarenkorbId = cdWarenkorbId;
        this.downloadWarenkorbId = downloadWarenkorbId;
    }

    public BestellungId getBestellungId() {
        return bestellungId;
    }

    public Hoerernummer getHoerernummer() {
        return hoerernummer;
    }

    public WarenkorbId getCdWarenkorbId() {
        return cdWarenkorbId;
    }

    public WarenkorbId getDownloadWarenkorbId() {
        return downloadWarenkorbId;
    }

}

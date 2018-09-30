/*
 * Copyright (C) 2011-2018 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchkatalog.domain.bestellung;

import wbh.bookworm.hoerbuchkatalog.domain.event.HoerbuchEvent;
import wbh.bookworm.hoerbuchkatalog.domain.hoerer.Hoerernummer;
import wbh.bookworm.hoerbuchkatalog.domain.katalog.Titelnummer;

public final class DownloadAusDemWarenkorbEntfernt extends HoerbuchEvent {

    private static final long serialVersionUID = -1L;

    DownloadAusDemWarenkorbEntfernt(final Hoerernummer hoerernummer, final Titelnummer titelnummer) {
        super(hoerernummer, titelnummer);
    }

}

/*
 * Copyright (C) 2011-2018 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchkatalog.domain.bestellung;

import wbh.bookworm.hoerbuchkatalog.domain.hoerer.Hoerernummer;
import wbh.bookworm.hoerbuchkatalog.domain.katalog.Titelnummer;

import java.util.Set;

/**
 * Event
 */
public final class DownloadWarenkorbBestellt extends WarenkorbBestellt {

    private static final long serialVersionUID = -1L;

    public DownloadWarenkorbBestellt(final Hoerernummer hoerernummer,
                                     final Set<Titelnummer> titelnummern) {
        super(hoerernummer, titelnummern);
    }

}

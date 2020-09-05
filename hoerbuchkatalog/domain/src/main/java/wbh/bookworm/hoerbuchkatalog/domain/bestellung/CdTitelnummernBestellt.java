/*
 * Copyright (C) 2011-2018 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchkatalog.domain.bestellung;

import java.util.Set;

import wbh.bookworm.shared.domain.Hoerernummer;
import wbh.bookworm.shared.domain.Titelnummer;

/**
 * Event
 */
public final class CdTitelnummernBestellt extends TitelnummernBestellt {

    private static final long serialVersionUID = -1L;

    CdTitelnummernBestellt(final Hoerernummer hoerernummer,
                           final Set<Titelnummer> titelnummern) {
        super(hoerernummer, titelnummern);
    }

}

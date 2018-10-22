/*
 * Copyright (C) 2011-2018 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchkatalog.domain.katalog;

import wbh.bookworm.hoerbuchkatalog.domain.hoerer.Hoerernummer;

public abstract class HoerbuchEvent extends HoerbuchkatalogEvent {

    private static final long serialVersionUID = -1L;

    private final Titelnummer titelnummer;

    public HoerbuchEvent(final Hoerernummer hoerernummer, final Titelnummer titelnummer) {
        super(hoerernummer);
        this.titelnummer = titelnummer;
    }

    public Titelnummer getTitelnummer() {
        return titelnummer;
    }

}

/*
 * Copyright (C) 2011-2018 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchkatalog.domain.katalog;

import java.util.Objects;

import wbh.bookworm.shared.domain.Hoerernummer;

public abstract class HoerbuchkatalogHoererEvent extends HoerbuchkatalogEvent {

    private static final long serialVersionUID = -1L;

    private final Hoerernummer hoerernummer;

    public HoerbuchkatalogHoererEvent(final Hoerernummer hoerernummer) {
        Objects.requireNonNull(hoerernummer);
        this.hoerernummer = hoerernummer;
    }

    public Hoerernummer getHoerernummer() {
        return hoerernummer;
    }

}

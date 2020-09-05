/*
 * Copyright (C) 2011-2019 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchkatalog.domain.hoerer;

import wbh.bookworm.shared.domain.Hoerernummer;

public final class HoererdatenAktualisiert extends HoererdatenEvent {

    private static final long serialVersionUID = -1L;

    public HoererdatenAktualisiert(final Hoerernummer hoerernummer) {
        super(hoerernummer);
    }

}

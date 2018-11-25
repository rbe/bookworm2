/*
 * Copyright (C) 2011-2018 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchkatalog.app.bestellung;

import wbh.bookworm.hoerbuchkatalog.domain.katalog.Hoerbuch;

import java.io.Serializable;
import java.util.List;

public final class HoerbuchListeDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private final List<Hoerbuch> hoerbuecher;

    public HoerbuchListeDTO(final List<Hoerbuch> hoerbuecher) {
        this.hoerbuecher = hoerbuecher;
    }

    public List<Hoerbuch> getHoerbuecher() {
        return hoerbuecher;
    }

}

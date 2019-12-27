/*
 * Copyright (C) 2011-2018 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchkatalog.domain.katalog;

import aoc.mikrokosmos.ddd.model.DomainId;

public class HoerbuchkatalogId extends DomainId<String> {

    public HoerbuchkatalogId(final String gesamtDatDateiname) {
        super(gesamtDatDateiname);
    }

}

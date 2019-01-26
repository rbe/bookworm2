/*
 * Copyright (C) 2011-2019 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchkatalog.domain.lieferung;

import wbh.bookworm.hoerbuchkatalog.domain.katalog.Titelnummer;

import aoc.ddd.model.DomainValueObject;

import java.time.LocalDate;
import java.util.List;

public final class Bestellkarte extends DomainValueObject {

    private static final long serialVersionUID = -1L;

    private final transient List<Titelnummer> titelnummern;

    private final LocalDate letztesBestelldatum;

    public Bestellkarte(final List<Titelnummer> titelnummern, final LocalDate letztesBestelldatum) {
        this.titelnummern = titelnummern;
        this.letztesBestelldatum = letztesBestelldatum;
    }

    public List<Titelnummer> getTitelnummern() {
        return titelnummern;
    }

    public LocalDate getLetztesBestelldatum() {
        return letztesBestelldatum;
    }

}

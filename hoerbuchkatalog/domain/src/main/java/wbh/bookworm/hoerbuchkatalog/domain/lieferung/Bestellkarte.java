/*
 * Copyright (C) 2011-2019 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchkatalog.domain.lieferung;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

import wbh.bookworm.shared.domain.Hoerernummer;
import wbh.bookworm.shared.domain.Titelnummer;

import aoc.mikrokosmos.ddd.model.DomainValueObject;

public final class Bestellkarte extends DomainValueObject {

    private static final long serialVersionUID = -1L;

    private final Hoerernummer hoerernummer;

    private final Titelnummer titelnummer;

    private final LocalDate letztesBestelldatum;

    public Bestellkarte(final Hoerernummer hoerernummer,
                        final Titelnummer titelnummer, final LocalDate letztesBestelldatum) {
        this.hoerernummer = hoerernummer;
        this.titelnummer = titelnummer;
        this.letztesBestelldatum = letztesBestelldatum;
    }

    public Hoerernummer getHoerernummer() {
        return hoerernummer;
    }

    public Titelnummer getTitelnummer() {
        return titelnummer;
    }

    public LocalDate getLetztesBestelldatum() {
        return letztesBestelldatum;
    }

    public String getLetztesBestelldatumAufDeutsch() {
        return null != letztesBestelldatum
                ? letztesBestelldatum.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"))
                : "";
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final Bestellkarte that = (Bestellkarte) o;
        return hoerernummer.equals(that.hoerernummer) &&
                titelnummer.equals(that.titelnummer) &&
                letztesBestelldatum.equals(that.letztesBestelldatum);
    }

    @Override
    public int hashCode() {
        return Objects.hash(hoerernummer, titelnummer, letztesBestelldatum);
    }

    @Override
    public String toString() {
        return String.format("Bestellkarte{hoerernummer=%s, titelnummer=%s, letztesBestelldatum=%s}",
                hoerernummer, titelnummer, letztesBestelldatum);
    }

}

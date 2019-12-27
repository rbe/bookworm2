/*
 * Copyright (C) 2011-2019 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchkatalog.domain.lieferung;

import wbh.bookworm.hoerbuchkatalog.domain.hoerer.Hoerernummer;
import wbh.bookworm.hoerbuchkatalog.domain.katalog.Titelnummer;

import aoc.mikrokosmos.ddd.model.DomainValueObject;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public final class ErledigteBestellkarte extends DomainValueObject {

    private static final long serialVersionUID = -1L;

    private final Hoerernummer hoerernummer;

    private final Titelnummer titelnummer;

    private final LocalDate ausleihdatum;

    public ErledigteBestellkarte(final Hoerernummer hoerernummer,
                                 final Titelnummer titelnummer, final LocalDate ausleihdatum) {
        this.hoerernummer = hoerernummer;
        this.titelnummer = titelnummer;
        this.ausleihdatum = ausleihdatum;
    }

    public Hoerernummer getHoerernummer() {
        return hoerernummer;
    }

    public Titelnummer getTitelnummer() {
        return titelnummer;
    }

    public LocalDate getAusleihdatum() {
        return ausleihdatum;
    }

    public String getAusleihdatumAufDeutsch() {
        return null != ausleihdatum
                ? ausleihdatum.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"))
                : "";
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final ErledigteBestellkarte that = (ErledigteBestellkarte) o;
        return hoerernummer.equals(that.hoerernummer) &&
                titelnummer.equals(that.titelnummer) &&
                Objects.equals(ausleihdatum, that.ausleihdatum);
    }

    @Override
    public int hashCode() {
        return Objects.hash(hoerernummer, titelnummer, ausleihdatum);
    }

    @Override
    public String toString() {
        return String.format("ErledigteBestellkarte{hoerernummer=%s, titelnummer=%s, ausleihdatum=%s}",
                hoerernummer, titelnummer, ausleihdatum);
    }

}

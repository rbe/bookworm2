/*
 * Copyright (C) 2011-2019 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchkatalog.domain.lieferung;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

import wbh.bookworm.shared.domain.Titelnummer;

import aoc.mikrokosmos.ddd.model.DomainValueObject;

public final class Belastung extends DomainValueObject {

    private static final long serialVersionUID = -1L;

    private LocalDate belastungsdatum;

    private String boxnummer;

    private Titelnummer titelnummer;

    public Belastung(final LocalDate belastungsdatum,
                     final String boxnummer, final Titelnummer titelnummer) {
        this.belastungsdatum = belastungsdatum;
        this.boxnummer = boxnummer;
        this.titelnummer = titelnummer;
    }

    public Belastung(final LocalDate belastungsdatum, final String boxnummer, final String titelnummer) {
        this(belastungsdatum, boxnummer, new Titelnummer(titelnummer));
    }

    public LocalDate getBelastungsdatum() {
        return belastungsdatum;
    }

    public String getBelastungsdatumAufDeutsch() {
        return null != belastungsdatum
                ? belastungsdatum.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"))
                : "";
    }

    public String getBoxnummer() {
        return boxnummer;
    }

    public Titelnummer getTitelnummer() {
        return titelnummer;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final Belastung belastung = (Belastung) o;
        return belastungsdatum.equals(belastung.belastungsdatum) &&
                boxnummer.equals(belastung.boxnummer) &&
                titelnummer.equals(belastung.titelnummer);
    }

    @Override
    public int hashCode() {
        return Objects.hash(belastungsdatum, boxnummer, titelnummer);
    }

    @Override
    public String toString() {
        return String.format("Belastung{belastungsdatum=%s, boxnummer='%s', titelnummer=%s}",
                belastungsdatum, boxnummer, titelnummer);
    }

}

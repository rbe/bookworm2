/*
 * Copyright (C) 2011-2019 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchkatalog.domain.lieferung;

import wbh.bookworm.hoerbuchkatalog.domain.hoerer.Hoerernummer;
import wbh.bookworm.hoerbuchkatalog.domain.katalog.Titelnummer;

import aoc.ddd.model.DomainValueObject;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public final class ErledigteBestellkarte extends DomainValueObject {

    private static final long serialVersionUID = -1L;

    private final Hoerernummer hoerernummer;

    private final Titelnummer titelnummer;

    private final LocalDate ausleihdatum;

    private ErledigteBestellkarte(final Hoerernummer hoerernummer,
                                  final Titelnummer titelnummer, final LocalDate ausleihdatum) {
        this.hoerernummer = hoerernummer;
        this.titelnummer = titelnummer;
        this.ausleihdatum = ausleihdatum;
    }

    public static ErledigteBestellkarte of(final String hoerernummer,
                                           final String titelnummer, final String ausleihdatum) {
        LocalDate _ausleihdatum = null;
        if (!ausleihdatum.equals("0")) {
            _ausleihdatum = LocalDate.parse(ausleihdatum, DateTimeFormatter.BASIC_ISO_DATE);
        }
        return new ErledigteBestellkarte(new Hoerernummer(hoerernummer),
                new Titelnummer(titelnummer), _ausleihdatum);
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

}

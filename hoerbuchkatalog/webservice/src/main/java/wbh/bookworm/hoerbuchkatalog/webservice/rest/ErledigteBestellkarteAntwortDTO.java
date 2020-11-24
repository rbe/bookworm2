/*
 * Copyright (C) 2011-2019 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchkatalog.webservice.rest;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public final class ErledigteBestellkarteAntwortDTO {

    private String hoerernummer;

    private String titelnummer;

    private LocalDate ausleihdatum;

    public String getHoerernummer() {
        return hoerernummer;
    }

    public void setHoerernummer(final String hoerernummer) {
        this.hoerernummer = hoerernummer;
    }

    public String getTitelnummer() {
        return titelnummer;
    }

    public void setTitelnummer(final String titelnummer) {
        this.titelnummer = titelnummer;
    }

    public LocalDate getAusleihdatum() {
        return ausleihdatum;
    }

    public void setAusleihdatum(final LocalDate ausleihdatum) {
        this.ausleihdatum = ausleihdatum;
    }

    public String getAusleihdatumAufDeutsch() {
        return null != ausleihdatum
                ? ausleihdatum.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"))
                : "";
    }

}

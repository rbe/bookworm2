/*
 * Copyright (C) 2011-2019 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchkatalog.webservice.rest;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public final class BestellkarteAntwortDTO {

    private String hoerernummer;

    private String titelnummer;

    private String autor;

    private String titel;

    private LocalDate letztesBestelldatum;

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

    public String getAutor() {
        return autor;
    }

    public void setAutor(final String autor) {
        this.autor = autor;
    }

    public String getTitel() {
        return titel;
    }

    public void setTitel(final String titel) {
        this.titel = titel;
    }

    public LocalDate getLetztesBestelldatum() {
        return letztesBestelldatum;
    }

    public void setLetztesBestelldatum(final LocalDate letztesBestelldatum) {
        this.letztesBestelldatum = letztesBestelldatum;
    }

    public String getLetztesBestelldatumAufDeutsch() {
        return null != letztesBestelldatum
                ? letztesBestelldatum.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"))
                : "";
    }

}

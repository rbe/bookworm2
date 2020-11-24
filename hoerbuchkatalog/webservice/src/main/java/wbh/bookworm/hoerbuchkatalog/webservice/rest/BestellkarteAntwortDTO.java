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

/*
 * Copyright (C) 2011-2019 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchkatalog.webservice.rest;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public final class BelastungAntwortDTO {

    private LocalDate belastungsdatum;

    private String boxnummer;

    private String titelnummer;

    public LocalDate getBelastungsdatum() {
        return belastungsdatum;
    }

    public void setBelastungsdatum(final LocalDate belastungsdatum) {
        this.belastungsdatum = belastungsdatum;
    }

    public String getBelastungsdatumAufDeutsch() {
        return null != belastungsdatum
                ? belastungsdatum.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"))
                : "";
    }

    public String getBoxnummer() {
        return boxnummer;
    }

    public void setBoxnummer(final String boxnummer) {
        this.boxnummer = boxnummer;
    }

    public String getTitelnummer() {
        return titelnummer;
    }

    public void setTitelnummer(final String titelnummer) {
        this.titelnummer = titelnummer;
    }

}

/*
 * Copyright (C) 2011-2019 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchkatalog.webservice.belastung;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public final class Belastung {

    private LocalDate belastungsdatum;

    private String boxnummer;

    private String sachgebiet;

    private String sachgebietBezeichnung;

    private String titelnummer;

    private String autor;

    private String titel;

    private String sprecher1;

    private String sprecher2;

    private String spieldauer;

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

    public String getSachgebiet() {
        return sachgebiet;
    }

    public void setSachgebiet(final String sachgebiet) {
        this.sachgebiet = sachgebiet;
    }

    public String getSachgebietBezeichnung() {
        return sachgebietBezeichnung;
    }

    public void setSachgebietBezeichnung(final String sachgebietBezeichnung) {
        this.sachgebietBezeichnung = sachgebietBezeichnung;
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

    public String getSprecher1() {
        return sprecher1;
    }

    public void setSprecher1(final String sprecher1) {
        this.sprecher1 = sprecher1;
    }

    public String getSprecher2() {
        return sprecher2;
    }

    public void setSprecher2(final String sprecher2) {
        this.sprecher2 = sprecher2;
    }

    public String getSpieldauer() {
        return spieldauer;
    }

    public void setSpieldauer(final String spieldauer) {
        this.spieldauer = spieldauer;
    }

}

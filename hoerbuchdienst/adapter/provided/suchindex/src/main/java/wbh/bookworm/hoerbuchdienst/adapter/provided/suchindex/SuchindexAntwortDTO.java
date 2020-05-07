/*
 * Copyright (C) 2011-2020 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchdienst.adapter.provided.suchindex;

import java.io.Serializable;
import java.time.Duration;

public final class SuchindexAntwortDTO implements Serializable {

    private static final long serialVersionUID = -5138272137326494323L;

    private String titelnummer;

    private String titel;

    private String autor;

    private String sprecher;

    private Duration spieldauer;

    public SuchindexAntwortDTO() {
    }

    public String getTitelnummer() {
        return titelnummer;
    }

    public void setTitelnummer(final String titelnummer) {
        this.titelnummer = titelnummer;
    }

    public String getTitel() {
        return titel;
    }

    public void setTitel(final String titel) {
        this.titel = titel;
    }

    public String getAutor() {
        return autor;
    }

    public void setAutor(final String autor) {
        this.autor = autor;
    }

    public String getSprecher() {
        return sprecher;
    }

    public void setSprecher(final String sprecher) {
        this.sprecher = sprecher;
    }

    public Duration getSpieldauer() {
        return spieldauer;
    }

    public void setSpieldauer(final Duration spieldauer) {
        this.spieldauer = spieldauer;
    }

    @Override
    public String toString() {
        return String.format("AudiobookInfoDTO{titelnummer='%s', titel='%s', autor='%s', sprecher='%s', spieldauer=%s}",
                titelnummer, titel, autor, sprecher, spieldauer);
    }

}

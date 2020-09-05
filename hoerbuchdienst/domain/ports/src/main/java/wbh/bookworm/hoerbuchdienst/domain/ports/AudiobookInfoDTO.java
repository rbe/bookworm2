/*
 * Copyright (C) 2011-2020 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchdienst.domain.ports;

import java.io.Serializable;
import java.time.Duration;

public final class AudiobookInfoDTO implements Serializable {

    private static final long serialVersionUID = -5138272137326494323L;

    private final /*TODO Titelnummer*/ String titelnummer;

    private final String titel;

    private final String autor;

    private final String sprecher;

    private final Duration spieldauer;

    public AudiobookInfoDTO(final String titelnummer, final String titel, final String autor, final String sprecher, final Duration spieldauer) {
        this.titelnummer = titelnummer;
        this.titel = titel;
        this.autor = autor;
        this.sprecher = sprecher;
        this.spieldauer = spieldauer;
    }

    public String getTitelnummer() {
        return titelnummer;
    }

    public String getTitel() {
        return titel;
    }

    public String getAutor() {
        return autor;
    }

    public String getSprecher() {
        return sprecher;
    }

    public Duration getSpieldauer() {
        return spieldauer;
    }

    @Override
    public String toString() {
        return String.format("AudiobookInfoDTO{titelnummer='%s', titel='%s', autor='%s', sprecher='%s', spieldauer=%s}",
                titelnummer, titel, autor, sprecher, spieldauer);
    }

}

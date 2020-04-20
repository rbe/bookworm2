/*
 * Copyright (C) 2011-2020 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchdienst.adapter.provided.stream;

import java.io.Serializable;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public final class AudiobookAnfrageDTO implements Serializable {

    private static final long serialVersionUID = -1L;

    private final String mandant;
    
    private final String hoerernummer;

    private final String titelnummer;

    @JsonCreator
    public AudiobookAnfrageDTO(@JsonProperty("mandant") final String mandant,
                               @JsonProperty("hoerernummer") final String hoerernummer,
                               @JsonProperty("titelnummer") final String titelnummer) {
        this.mandant = mandant;
        this.hoerernummer = hoerernummer;
        this.titelnummer = titelnummer;
    }

    public String getMandant() {
        return mandant;
    }

    public String getHoerernummer() {
        return hoerernummer;
    }

    public String getTitelnummer() {
        return titelnummer;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final AudiobookAnfrageDTO that = (AudiobookAnfrageDTO) o;
        return mandant.equals(that.mandant) &&
                hoerernummer.equals(that.hoerernummer) &&
                titelnummer.equals(that.titelnummer);
    }

    @Override
    public int hashCode() {
        return Objects.hash(mandant, hoerernummer, titelnummer);
    }

    @Override
    public String toString() {
        return String.format("DownloadAnfrageDTO{mandant='%s', hoerernummer='%s', titelnummer='%s'}",
                mandant, hoerernummer, titelnummer);
    }

}

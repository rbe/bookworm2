/*
 * Copyright (C) 2011-2020 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchdienst.adapter.provided.stream;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.io.Serializable;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.micronaut.core.annotation.Introspected;

@Introspected
public final class BestellungAnfrageDTO implements Serializable {

    private static final long serialVersionUID = -1L;

    @Min(3)
    @Max(20)
    private final String mandant;

    @Min(2)
    @Max(20)
    private final String hoerernummer;

    @Min(13)
    @Max(13)
    private final String aghNummer;

    // TODO Titelnummer durch ...Resolver beim Mandanten auflösen
    @Min(1)
    @Max(5)
    private final String titelnummer;

    @JsonCreator
    public BestellungAnfrageDTO(@JsonProperty("mandant") final String mandant,
                                @JsonProperty("hoerernummer") final String hoerernummer,
                                @JsonProperty("aghNummer") final String aghNummer,
                                @JsonProperty("titelnummer") final String titelnummer) {
        this.mandant = mandant;
        this.hoerernummer = hoerernummer;
        this.aghNummer = aghNummer;
        this.titelnummer = titelnummer;
    }

    public String getMandant() {
        return mandant;
    }

    public String getHoerernummer() {
        return hoerernummer;
    }

    public String getAghNummer() {
        return aghNummer;
    }

    public String getTitelnummer() {
        return titelnummer;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final BestellungAnfrageDTO that = (BestellungAnfrageDTO) o;
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

/*
 * Copyright (C) 2011-2020 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchdienst.adapter.provided.katalog;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.micronaut.core.annotation.Introspected;

@Introspected
public final class TrackAnfrageDTO implements Serializable {

    private static final long serialVersionUID = -1L;

    @NotBlank
    @Min(3)
    private final String mandant;

    // TODO Validation ist mandatenspezifisch
    @NotBlank
    @Min(3)
    private final String hoerernummer;

    // TODO Validation ist mandatenspezifisch
    @NotBlank
    @Min(5)
    private final String titelnummer;

    @NotBlank
    @Min(5)
    private final String ident;

    @JsonCreator
    public TrackAnfrageDTO(@JsonProperty("mandant") final String mandant,
                           @JsonProperty("hoerernummer") final String hoerernummer,
                           @JsonProperty("titelnummer") final String titelnummer,
                           @JsonProperty("ident") final String ident) {
        this.mandant = mandant;
        this.hoerernummer = hoerernummer;
        this.titelnummer = titelnummer;
        this.ident = ident;
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

    public String getIdent() {
        return ident;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final TrackAnfrageDTO that = (TrackAnfrageDTO) o;
        return mandant.equals(that.mandant) &&
                hoerernummer.equals(that.hoerernummer) &&
                titelnummer.equals(that.titelnummer) &&
                ident.equals(that.ident);
    }

    @Override
    public int hashCode() {
        return Objects.hash(mandant, hoerernummer, titelnummer, ident);
    }

    @Override
    public String toString() {
        return String.format("StreamAnfrageDTO{mandant='%s', hoerernummer='%s', titelnummer='%s', ident='%s'}",
                mandant, hoerernummer, titelnummer, ident);
    }

}

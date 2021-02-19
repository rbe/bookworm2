/*
 * Copyright (C) 2011-2018 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchkatalog.domain.hoerer;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import wbh.bookworm.shared.domain.Hoerernummer;

import aoc.mikrokosmos.ddd.model.DomainAggregate;

public final class HoererProfil extends DomainAggregate<HoererProfil, Hoerernummer> {

    private final Hoerernummer hoerernummer;

    private int anzahlBestellungenProAusleihzeitraum;

    private int anzahlBestellungenProTag;

    private int anzahlDownloads;

    public HoererProfil(final Hoerernummer hoerernummer) {
        super(hoerernummer);
        this.hoerernummer = hoerernummer;
        this.anzahlBestellungenProAusleihzeitraum = 0;
        this.anzahlBestellungenProTag = 0;
    }

    @JsonCreator
    public HoererProfil(@JsonProperty("domainId") final Hoerernummer hoerernummer,
                        @JsonProperty("anzahlBestellungenProAusleihzeitraum") final int anzahlBestellungenProAusleihzeitraum,
                        @JsonProperty("anzahlBestellungenProTag") final int anzahlBestellungenProTag) {
        super(hoerernummer);
        this.hoerernummer = hoerernummer;
        this.anzahlBestellungenProAusleihzeitraum = anzahlBestellungenProAusleihzeitraum;
        this.anzahlBestellungenProTag = anzahlBestellungenProTag;
    }

    public int getAnzahlBestellungenProAusleihzeitraum() {
        return anzahlBestellungenProAusleihzeitraum;
    }

    public void setAnzahlBestellungenProAusleihzeitraum(final int anzahlBestellungenProAusleihzeitraum) {
        this.anzahlBestellungenProAusleihzeitraum = anzahlBestellungenProAusleihzeitraum;
    }

    public int getAnzahlBestellungenProTag() {
        return anzahlBestellungenProTag;
    }

    public void setAnzahlBestellungenProTag(final int anzahlBestellungenProTag) {
        this.anzahlBestellungenProTag = anzahlBestellungenProTag;
    }

    public int getAnzahlDownloads() {
        return anzahlDownloads;
    }

    public void setAnzahlDownloads(final int anzahlDownloads) {
        this.anzahlDownloads = anzahlDownloads;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        final HoererProfil that = (HoererProfil) o;
        return anzahlBestellungenProAusleihzeitraum == that.anzahlBestellungenProAusleihzeitraum && anzahlBestellungenProTag == that.anzahlBestellungenProTag && anzahlDownloads == that.anzahlDownloads && hoerernummer.equals(that.hoerernummer);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), hoerernummer, anzahlBestellungenProAusleihzeitraum, anzahlBestellungenProTag, anzahlDownloads);
    }

    @Override
    public String toString() {
        return String.format("HoererProfil{anzahlBestellungenProAusleihzeitraum=%d, anzahlBestellungenProTag=%d, anzahlDownloads=%d}",
                anzahlBestellungenProAusleihzeitraum, anzahlBestellungenProTag, anzahlDownloads);
    }

}

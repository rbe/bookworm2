/*
 * Copyright (C) 2011-2018 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchkatalog.domain.bestellung;

import wbh.bookworm.hoerbuchkatalog.domain.hoerer.Hoerernummer;
import wbh.bookworm.platform.ddd.model.DomainAggregate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

public final class Bestellung extends DomainAggregate<Bestellung, BestellungId> {

    private static final long serialVersionUID = -1L;

    private static final Logger LOGGER = LoggerFactory.getLogger(Bestellung.class);

    private final Hoerernummer hoerernummer;

    private final String hoerername;

    private final String hoereremail;

    private final String bemerkung;

    private final Boolean bestellkarteMischen;

    private final Boolean alteBestellkarteLoeschen;

    private final WarenkorbId cdWarenkorbId;

    private final WarenkorbId downloadWarenkorbId;

    public Bestellung(final BestellungId bestellungId,
                      final Hoerernummer hoerernummer,
                      final String hoerername, final String hoereremail,
                      final String bemerkung,
                      final Boolean bestellkarteMischen, final Boolean alteBestellkarteLoeschen,
                      final WarenkorbId cdWarenkorbId, final WarenkorbId downloadWarenkorbId) {
        super(bestellungId);
        this.hoerername = hoerername;
        this.hoerernummer = hoerernummer;
        this.hoereremail = hoereremail;
        this.bemerkung = bemerkung;
        this.bestellkarteMischen = bestellkarteMischen;
        this.alteBestellkarteLoeschen = alteBestellkarteLoeschen;
        this.cdWarenkorbId = cdWarenkorbId;
        this.downloadWarenkorbId = downloadWarenkorbId;
    }

    public Hoerernummer getHoerernummer() {
        return hoerernummer;
    }

    public String getHoerername() {
        return hoerername;
    }

    public String getHoereremail() {
        return hoereremail;
    }

    public String getBemerkung() {
        return bemerkung;
    }

    public Boolean getBestellkarteMischen() {
        return bestellkarteMischen;
    }

    public Boolean getAlteBestellkarteLoeschen() {
        return alteBestellkarteLoeschen;
    }

    @Override
    public boolean equals(final Object other) {
        if (this == other) return true;
        if (other == null || getClass() != other.getClass()) return false;
        final Bestellung that = (Bestellung) other;
        return Objects.equals(hoerernummer, that.hoerernummer) &&
                Objects.equals(cdWarenkorbId, that.cdWarenkorbId) &&
                Objects.equals(downloadWarenkorbId, that.downloadWarenkorbId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(hoerernummer, cdWarenkorbId, downloadWarenkorbId);
    }

    @Override
    public int compareTo(final Bestellung other) {
        /* TODO Comparable */return other.hoerernummer.compareTo(this.hoerernummer.getValue());
    }

    @Override
    public String toString() {
        return String.format("Bestellung{domainId=%s, hoerernummer=%s, cdWarenkorbId=%s, downloadWarenkorbId=%s}",
                domainId, hoerernummer, cdWarenkorbId, downloadWarenkorbId);
    }

}

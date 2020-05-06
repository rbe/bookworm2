/*
 * Copyright (C) 2011-2018 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchkatalog.domain.bestellung;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import wbh.bookworm.hoerbuchkatalog.domain.hoerer.HoererEmail;
import wbh.bookworm.hoerbuchkatalog.domain.hoerer.Hoerername;
import wbh.bookworm.shared.domain.hoerbuch.Titelnummer;
import wbh.bookworm.shared.domain.hoerer.Hoerernummer;

import aoc.mikrokosmos.ddd.event.DomainEventPublisher;
import aoc.mikrokosmos.ddd.model.DomainAggregate;

public final class Bestellung extends DomainAggregate<Bestellung, BestellungId> {

    private static final long serialVersionUID = -1L;

    private static final Logger LOGGER = LoggerFactory.getLogger(Bestellung.class);

    private final Hoerernummer hoerernummer;

    private final Hoerername hoerername;

    private final HoererEmail hoereremail;

    private final String bemerkung;

    private final Boolean bestellkarteMischen;

    private final Boolean alteBestellkarteLoeschen;

    private final Set<Titelnummer> cdTitelnummern;

    private final Set<Titelnummer> downloadTitelnummern;

    private LocalDateTime zeitpunktAbgeschickt;

    @JsonCreator
    public Bestellung(final @JsonProperty("domainId") BestellungId bestellungId,
                      final @JsonProperty("hoerernummer") Hoerernummer hoerernummer,
                      final @JsonProperty("hoerername") Hoerername hoerername,
                      final @JsonProperty("hoereremail") HoererEmail hoereremail,
                      final @JsonProperty("bemerkung") String bemerkung,
                      final @JsonProperty("bestellkarteMischen") Boolean bestellkarteMischen,
                      final @JsonProperty("alteBestellkarteLoeschen") Boolean alteBestellkarteLoeschen,
                      final @JsonProperty("cdTitelnummern") Set<Titelnummer> cdTitelnummern,
                      final @JsonProperty("downloadTitelnummern") Set<Titelnummer> downloadTitelnummern,
                      final @JsonProperty("zeitpunktAbgeschickt") LocalDateTime zeitpunktAbgeschickt) {
        super(bestellungId);
        this.hoerername = hoerername;
        this.hoerernummer = hoerernummer;
        this.hoereremail = hoereremail;
        this.bemerkung = bemerkung;
        this.bestellkarteMischen = bestellkarteMischen;
        this.alteBestellkarteLoeschen = alteBestellkarteLoeschen;
        this.cdTitelnummern = cdTitelnummern;
        this.downloadTitelnummern = downloadTitelnummern;
        this.zeitpunktAbgeschickt = zeitpunktAbgeschickt;
    }

    @SuppressWarnings({"squid:S00107"})
    public Bestellung(final BestellungId bestellungId,
                      final Hoerernummer hoerernummer,
                      final Hoerername hoerername,
                      final HoererEmail hoereremail,
                      final String bemerkung,
                      final Boolean bestellkarteMischen,
                      final Boolean alteBestellkarteLoeschen,
                      final Set<Titelnummer> cdTitelnummern, final Set<Titelnummer> downloadTitelnummern) {
        this(bestellungId,
                hoerernummer, hoerername, hoereremail,
                bemerkung,
                bestellkarteMischen, alteBestellkarteLoeschen,
                new TreeSet<>(cdTitelnummern), new TreeSet<>(downloadTitelnummern),
                null);
    }

    public Hoerernummer getHoerernummer() {
        return hoerernummer;
    }

    public Hoerername getHoerername() {
        return hoerername;
    }

    public HoererEmail getHoereremail() {
        return hoereremail;
    }

    public boolean hasBemerkung() {
        return null != bemerkung && !bemerkung.isBlank();
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

    public Set<Titelnummer> getCdTitelnummern() {
        return cdTitelnummern;
    }

    public boolean hatCdTitelnummern() {
        return !cdTitelnummern.isEmpty();
    }

    public Set<Titelnummer> getDownloadTitelnummern() {
        return downloadTitelnummern;
    }

    public boolean hatDownloadTitelnummern() {
        return !downloadTitelnummern.isEmpty();
    }

    public LocalDateTime aufgeben() {
        if (null == zeitpunktAbgeschickt) {
            LOGGER.info("Bestellung {} für Hörer {} wird aufgegeben",
                    domainId, hoerernummer);
            zeitpunktAbgeschickt = LocalDateTime.now();
            DomainEventPublisher.global()
                    .publishAsync(new BestellungAufgegeben(hoerernummer, this));
        } else {
            LOGGER.error("Bestellung {} für Hörer {} wurde bereits abgeschickt",
                    domainId, hoerernummer);
        }
        return zeitpunktAbgeschickt;
    }

    public LocalDateTime getZeitpunktAbgeschickt() {
        return zeitpunktAbgeschickt;
    }

    @JsonIgnore
    public String getZeitpunktAbgeschicktAlsDeutschesDatum() {
        return zeitpunktAbgeschickt.format(DateTimeFormatter.ofPattern("dd.MM.yyyy 'um' HH:mm"));
    }

    public boolean inAktuellemMonatAbgeschickt() {
        if (null == zeitpunktAbgeschickt) {
            return false;
        } else {
            final LocalDateTime now = LocalDateTime.now();
            return zeitpunktAbgeschickt.getYear() == now.getYear()
                    && zeitpunktAbgeschickt.getMonth() == now.getMonth();
        }
    }

    public boolean heuteAbgeschickt() {
        if (null == zeitpunktAbgeschickt) {
            return false;
        } else {
            final LocalDateTime now = LocalDateTime.now();
            return zeitpunktAbgeschickt.getYear() == now.getYear()
                    && zeitpunktAbgeschickt.getMonth() == now.getMonth()
                    && zeitpunktAbgeschickt.getDayOfMonth() == now.getDayOfMonth();
        }
    }

    @Override
    public boolean equals(final Object other) {
        if (this == other) return true;
        if (other == null || getClass() != other.getClass()) return false;
        final Bestellung that = (Bestellung) other;
        return Objects.equals(domainId, that.domainId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(hoerernummer, cdTitelnummern, downloadTitelnummern);
    }

    @Override
    public int compareTo(final Bestellung other) {
        return other.domainId.compareTo(this.domainId.getValue());
    }

    @Override
    public String toString() {
        return String.format("Bestellung{domainId=%s, hoerernummer=%s, cdTitelnummern=%s, downloadTitelnummern=%s}",
                domainId, hoerernummer, cdTitelnummern, downloadTitelnummern);
    }

}

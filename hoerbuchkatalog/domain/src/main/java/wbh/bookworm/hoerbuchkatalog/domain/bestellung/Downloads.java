/*
 * Copyright (C) 2011-2018 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchkatalog.domain.bestellung;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import wbh.bookworm.shared.domain.Hoerernummer;
import wbh.bookworm.shared.domain.Titelnummer;

import aoc.mikrokosmos.ddd.model.DomainAggregate;

public final class Downloads extends DomainAggregate<Downloads, DownloadsId> {

    private static final Logger LOGGER = LoggerFactory.getLogger(Downloads.class);

    @JsonProperty
    private final Hoerernummer hoerernummer;

    private final Map<Titelnummer, LocalDate> titelnummern;

    /**
     * Copy constructor
     */
    public Downloads(final Downloads downloads) {
        super(new DownloadsId(downloads.domainId.getValue()));
        this.hoerernummer = new Hoerernummer(downloads.hoerernummer.getValue());
        this.titelnummern = new HashMap<>();
        this.titelnummern.putAll(downloads.titelnummern);
    }

    public Downloads(final DownloadsId downloadsId, final Hoerernummer hoerernummer) {
        this(downloadsId, hoerernummer, new HashMap<>());
    }

    @JsonCreator
    public Downloads(final @JsonProperty("domainId") DownloadsId downloadsId,
                     final @JsonProperty("hoerernummer") Hoerernummer hoerernummer,
                     final @JsonProperty("titelnummern") Map<Titelnummer, LocalDate> titelnummern) {
        super(downloadsId);
        this.hoerernummer = hoerernummer;
        this.titelnummern = titelnummern;
    }

    public Map<Titelnummer, LocalDate> getTitelnummern() {
        final LocalDate now = LocalDate.now();
        return titelnummern.entrySet().stream()
                .filter(e -> now.getMonthValue() == e.getValue().getMonthValue()
                        && now.getYear() == e.getValue().getYear())
                .collect(Collectors.toUnmodifiableMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    @JsonIgnore
    public int getAnzahl() {
        return titelnummern.size();
    }

    public boolean enthalten(final Titelnummer titelnummer) {
        final boolean bereitsVorhanden = titelnummern.containsKey(titelnummer);
        LOGGER.trace("Downloads {} enthält Hörbuch {}: {}", this, titelnummer, bereitsVorhanden);
        return bereitsVorhanden;
    }

    public void hinzufuegen(final Titelnummer titelnummer) {
        titelnummern.put(titelnummer, LocalDate.now());
        LOGGER.info("Hörbuch {} zu Downloads {} hinzugefügt", titelnummer, this);
    }

    public void entfernen(final Titelnummer titelnummer) {
        titelnummern.remove(titelnummer);
        LOGGER.info("Hörbuch {} von Downloads {} entfernt", titelnummer, this);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        final Downloads merkliste = (Downloads) o;
        return Objects.equals(hoerernummer, merkliste.hoerernummer) &&
                Objects.equals(titelnummern, merkliste.titelnummern);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), hoerernummer, titelnummern);
    }

    @Override
    public String toString() {
        return String.format("Merkliste{domainId=%s, merklisteId=%s, hoerernummer=%s, titelnummern=%s}",
                domainId, domainId, hoerernummer, titelnummern);
    }

}

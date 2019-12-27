/*
 * Copyright (C) 2011-2018 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchkatalog.domain.bestellung;

import wbh.bookworm.hoerbuchkatalog.domain.hoerer.Hoerernummer;
import wbh.bookworm.hoerbuchkatalog.domain.katalog.Titelnummer;

import aoc.mikrokosmos.ddd.event.DomainEventPublisher;
import aoc.mikrokosmos.ddd.model.DomainAggregate;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;

public final class Merkliste extends DomainAggregate<Merkliste, MerklisteId> {

    private static final Logger LOGGER = LoggerFactory.getLogger(Merkliste.class);

    @JsonProperty
    private final Hoerernummer hoerernummer;

    private final Set<Titelnummer> titelnummern;

    /** Copy constructor */
    public Merkliste(final Merkliste merkliste) {
        super(new MerklisteId(merkliste.domainId.getValue()));
        this.hoerernummer = new Hoerernummer(merkliste.hoerernummer.getValue());
        this.titelnummern = new TreeSet<>();
        merkliste.titelnummern.forEach(titelnummer ->
                this.titelnummern.add(new Titelnummer(titelnummer.getValue())));
    }

    public Merkliste(final MerklisteId merklisteId, final Hoerernummer hoerernummer) {
        this(merklisteId, hoerernummer, new TreeSet<>());
    }

    @JsonCreator
    public Merkliste(final @JsonProperty("domainId") MerklisteId merklisteId,
                     final @JsonProperty("hoerernummer") Hoerernummer hoerernummer,
                     final @JsonProperty("titelnummern") Set<Titelnummer> titelnummern) {
        super(merklisteId);
        this.hoerernummer = hoerernummer;
        this.titelnummern = titelnummern;
    }

    public Set<Titelnummer> getTitelnummern() {
        return titelnummern;
    }

    @JsonIgnore
    public int getAnzahl() {
        return titelnummern.size();
    }

    public boolean enthalten(final Titelnummer titelnummer) {
        final boolean bereitsVorhanden = titelnummern.contains(titelnummer);
        LOGGER.trace("Merkliste {} enthält Hörbuch {}: {}", this, titelnummer, bereitsVorhanden);
        return bereitsVorhanden;
    }

    public void hinzufuegen(final Titelnummer titelnummer) {
        titelnummern.add(titelnummer);
        LOGGER.info("Hörbuch {} zum Warenkorb {} hinzugefügt", titelnummer, this);
        DomainEventPublisher.global()
                .publishAsync(new HoerbuechAufDieMerklisteGesetzt(hoerernummer, this, titelnummer));
    }

    public void entfernen(final Titelnummer titelnummer) {
        titelnummern.remove(titelnummer);
        LOGGER.info("Hörbuch {} aus der Merkliste {} entfernt", titelnummer, this);
        DomainEventPublisher.global()
                .publishAsync(new HoerbuechVonDerMerklisteEntfernt(hoerernummer, this, titelnummer));
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        final Merkliste merkliste = (Merkliste) o;
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

/*
 * Copyright (C) 2011-2018 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchkatalog.domain.bestellung;

import wbh.bookworm.hoerbuchkatalog.domain.hoerer.Hoerernummer;
import wbh.bookworm.hoerbuchkatalog.domain.katalog.Titelnummer;
import wbh.bookworm.platform.ddd.event.DomainEventPublisher;
import wbh.bookworm.platform.ddd.model.DomainAggregate;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;

public final class Merkliste extends DomainAggregate<Merkliste, MerklisteId> {

    private static final Logger LOGGER = LoggerFactory.getLogger(Merkliste.class);

    @JsonProperty
    private final MerklisteId merklisteId;

    @JsonProperty
    private final Hoerernummer hoerernummer;

    private final Set<Titelnummer> titelnummern;

    public Merkliste(final MerklisteId merklisteId, final Hoerernummer hoerernummer) {
        this(merklisteId, hoerernummer, new TreeSet<>());
    }

    @JsonCreator
    public Merkliste(final @JsonProperty("domainId") MerklisteId merklisteId,
                     final @JsonProperty("hoerernummer") Hoerernummer hoerernummer,
                     final @JsonProperty("titelnummern") Set<Titelnummer> titelnummern) {
        this.merklisteId = merklisteId;
        this.hoerernummer = hoerernummer;
        this.titelnummern = titelnummern;
    }

    public Set<Titelnummer> getTitelnummern() {
        return titelnummern;
    }

    public int getAnzahl() {
        return titelnummern.size();
    }

    public boolean enthalten(final Titelnummer titelnummer) {
        return titelnummern.contains(titelnummer);
    }

    public void hinzufuegen(final Titelnummer titelnummer) {
        titelnummern.add(titelnummer);
        LOGGER.info("Hörbuch#{} zum Warenkorb {} hinzugefügt", titelnummer, this);
        DomainEventPublisher.global()
                .publish(new HoerbuechAufDieMerklisteGesetzt(hoerernummer, titelnummer));
    }

    public void entfernen(final Titelnummer titelnummer) {
        titelnummern.remove(titelnummer);
        LOGGER.info("Hörbuch#{} aus dem Warenkorb {} entfernt", titelnummer, this);
        DomainEventPublisher.global()
                .publish(new HoerbuechVonDerMerklisteEntfernt(hoerernummer, titelnummer));
    }

    @Override
    public boolean equals(final Object other) {
        if (this == other) return true;
        if (other == null || getClass() != other.getClass()) return false;
        final Merkliste merkliste = (Merkliste) other;
        return Objects.equals(merklisteId, merkliste.merklisteId) &&
                Objects.equals(hoerernummer, merkliste.hoerernummer);
    }

    @Override
    public int hashCode() {
        return Objects.hash(merklisteId, hoerernummer);
    }

    @Override
    public String toString() {
        return String.format("Merkliste{domainId=%s, merklisteId=%s, hoerernummer=%s, titelnummern=%s}",
                domainId, merklisteId, hoerernummer, titelnummern);
    }

}

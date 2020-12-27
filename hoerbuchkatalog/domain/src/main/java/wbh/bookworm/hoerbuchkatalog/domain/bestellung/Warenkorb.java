/*
 * Copyright (C) 2011-2018 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchkatalog.domain.bestellung;

import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import wbh.bookworm.shared.domain.Hoerernummer;
import wbh.bookworm.shared.domain.Titelnummer;

import aoc.mikrokosmos.ddd.event.DomainEventPublisher;
import aoc.mikrokosmos.ddd.model.DomainAggregate;

public abstract class Warenkorb extends DomainAggregate<Warenkorb, WarenkorbId> {

    private static final long serialVersionUID = 1L;

    protected final transient Logger logger = LoggerFactory.getLogger(Warenkorb.class);

    @JsonProperty
    protected final Hoerernummer hoerernummer;

    @JsonProperty
    protected transient Set<Titelnummer> titelnummern;

    /** Copy constructor */
    public Warenkorb(final Warenkorb warenkorb) {
        super(new WarenkorbId(warenkorb.domainId.getValue()));
        this.hoerernummer = new Hoerernummer(warenkorb.hoerernummer.getValue());
        this.titelnummern = new TreeSet<>();
        warenkorb.titelnummern.forEach(titelnummer ->
                this.titelnummern.add(new Titelnummer(titelnummer.getValue())));
    }

    public Warenkorb(final WarenkorbId warenkorbId, final Hoerernummer hoerernummer) {
        this(warenkorbId, hoerernummer, new TreeSet<>());
    }

    @JsonCreator
    public Warenkorb(final @JsonProperty("domainId") WarenkorbId warenkorbId,
                     final @JsonProperty("hoerernummer") Hoerernummer hoerernummer,
                     final @JsonProperty("titelnummern") Set<Titelnummer> titelnummern) {
        super(warenkorbId);
        this.hoerernummer = hoerernummer;
        this.titelnummern = titelnummern;
    }

    public Hoerernummer getHoerernummer() {
        return hoerernummer;
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
        logger.trace("Warenkorb '{}' enthält Hörbuch '{}': '{}'", this, titelnummer, bereitsVorhanden);
        return bereitsVorhanden;
    }

    public void hinzufuegen(final Titelnummer titelnummer) {
        titelnummern.add(titelnummer);
        logger.info("Hörbuch '{}' zum Warenkorb '{}' hinzugefügt", titelnummer, this);
    }

    public void entfernen(final Titelnummer titelnummer) {
        titelnummern.remove(titelnummer);
        logger.info("Hörbuch '{}' aus dem Warenkorb '{}' entfernt", titelnummer, this);
    }

    public void leeren() {
        titelnummern.clear();
        DomainEventPublisher.global()
                .publishAsync(new WarenkorbGeleert(hoerernummer, this));
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        final Warenkorb warenkorb = (Warenkorb) o;
        return Objects.equals(hoerernummer, warenkorb.hoerernummer) &&
                Objects.equals(titelnummern, warenkorb.titelnummern);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), hoerernummer, titelnummern);
    }

    @Override
    public int compareTo(final Warenkorb other) {
        return other.domainId.compareTo(this.domainId.getValue());
    }

}

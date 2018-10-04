/*
 * Copyright (C) 2011-2018 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchkatalog.domain.bestellung;

import wbh.bookworm.hoerbuchkatalog.domain.hoerer.Hoerernummer;
import wbh.bookworm.hoerbuchkatalog.domain.katalog.Titelnummer;
import wbh.bookworm.platform.ddd.model.DomainAggregate;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;
import java.util.TreeSet;

public abstract class Warenkorb extends DomainAggregate<Warenkorb, WarenkorbId> {

    private static final long serialVersionUID = 1L;

    protected final Logger logger = LoggerFactory.getLogger(Warenkorb.class);

    @JsonProperty
    protected final Hoerernummer hoerernummer;

    protected Set<Titelnummer> titelnummern;

    public Warenkorb(final Warenkorb warenkorb) {
        this.domainId = new WarenkorbId(warenkorb.domainId.getValue());
        this.hoerernummer = new Hoerernummer(warenkorb.hoerernummer.getValue());
        this.titelnummern = new TreeSet<>();
        warenkorb.titelnummern.forEach(titelnummer ->
                this.titelnummern.add(new Titelnummer(titelnummer.getValue())));
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
        logger.trace("Warenkorb {} enthält Hörbuch#{}: {}", this, titelnummer, bereitsVorhanden);
        return bereitsVorhanden;
    }

    public void hinzufuegen(final Titelnummer titelnummer) {
        titelnummern.add(titelnummer);
        logger.info("Hörbuch#{} zum Warenkorb {} hinzugefügt", titelnummer, this);
    }

    public void entfernen(final Titelnummer titelnummer) {
        titelnummern.remove(titelnummer);
        logger.info("Hörbuch#{} aus dem Warenkorb {} entfernt", titelnummer, this);
    }

    public abstract void bestellen();

    public void leeren() {
        titelnummern.clear();
    }

    @Override
    public int compareTo(final Warenkorb other) {
        return other.domainId.compareTo(this.domainId.getValue());
    }

}

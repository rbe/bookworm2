/*
 * Copyright (C) 2011-2018 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchkatalog.domain.bestellung;

import wbh.bookworm.hoerbuchkatalog.domain.hoerer.Hoerernummer;
import wbh.bookworm.hoerbuchkatalog.domain.katalog.Titelnummer;

import aoc.ddd.event.DomainEventPublisher;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;

public final class CdWarenkorb extends Warenkorb {

    private static final long serialVersionUID = -1L;

    public CdWarenkorb(final CdWarenkorb cdWarenkorb) {
        super(cdWarenkorb);
    }

    public CdWarenkorb(final WarenkorbId warenkorbId, final Hoerernummer hoerernummer) {
        super(warenkorbId, hoerernummer, new TreeSet<>());
    }

    @JsonCreator/*(mode = JsonCreator.Mode.PROPERTIES)*/
    public CdWarenkorb(final @JsonProperty("domainId") WarenkorbId warenkorbId,
                       final @JsonProperty("hoerernummer") Hoerernummer hoerernummer,
                       final @JsonProperty("titelnummern") Set<Titelnummer> titelnummern) {
        super(warenkorbId, hoerernummer, titelnummern);
    }

    @Override
    public void hinzufuegen(final Titelnummer titelnummer) {
        super.hinzufuegen(titelnummer);
        DomainEventPublisher.global()
                .publish(new CdInDenWarenkorbGelegt(hoerernummer, this, titelnummer));
    }

    @Override
    public void entfernen(final Titelnummer titelnummer) {
        super.entfernen(titelnummer);
        DomainEventPublisher.global()
                .publish(new CdAusDemWarenkorbEntfernt(hoerernummer, this, titelnummer));
    }

    @Override
    public int hashCode() {
        return Objects.hash(domainId);
    }

    @Override
    public boolean equals(final Object other) {
        if (this == other) return true;
        if (other == null || getClass() != other.getClass()) return false;
        final CdWarenkorb that = (CdWarenkorb) other;
        return Objects.equals(domainId, that.domainId);
    }

    @Override
    public String toString() {
        return String.format("CdWarenkorb{domainId=%s, hoerernummer=%s, titelnummern=%s}",
                domainId, hoerernummer, titelnummern);
    }

}

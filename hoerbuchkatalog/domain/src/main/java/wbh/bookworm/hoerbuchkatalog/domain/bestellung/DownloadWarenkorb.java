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
import com.fasterxml.jackson.annotation.JsonProperty;

import wbh.bookworm.shared.domain.hoerbuch.Titelnummer;
import wbh.bookworm.shared.domain.mandant.Hoerernummer;

import aoc.mikrokosmos.ddd.event.DomainEventPublisher;

public final class DownloadWarenkorb extends Warenkorb {

    private static final long serialVersionUID = -1L;

    public DownloadWarenkorb(final DownloadWarenkorb downloadWarenkorb) {
        super(downloadWarenkorb);
    }

    public DownloadWarenkorb(final WarenkorbId warenkorbId, final Hoerernummer hoerernummer) {
        this(warenkorbId, hoerernummer, new TreeSet<>());
    }

    @JsonCreator/*(mode = JsonCreator.Mode.PROPERTIES)*/
    public DownloadWarenkorb(final @JsonProperty("domainId") WarenkorbId warenkorbId,
                             final @JsonProperty("hoerernummer") Hoerernummer hoerernummer,
                             final @JsonProperty("titelnummern") Set<Titelnummer> titelnummern) {
        super(warenkorbId, hoerernummer, titelnummern);
    }

    @Override
    public void hinzufuegen(final Titelnummer titelnummer) {
        super.hinzufuegen(titelnummer);
        DomainEventPublisher.global()
                .publishAsync(new DownloadInDenWarenkorbGelegt(hoerernummer, this, titelnummer));
    }

    @Override
    public void entfernen(final Titelnummer titelnummer) {
        super.entfernen(titelnummer);
        DomainEventPublisher.global()
                .publishAsync(new DownloadAusDemWarenkorbEntfernt(hoerernummer, this, titelnummer));
    }

    @Override
    public boolean equals(final Object other) {
        if (this == other) return true;
        if (other == null || getClass() != other.getClass()) return false;
        final DownloadWarenkorb that = (DownloadWarenkorb) other;
        return Objects.equals(domainId, that.domainId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(domainId);
    }

    @Override
    public String toString() {
        return String.format("DownloadWarenkorb{domainId=%s, hoerernummer=%s, titelnummern=%s}",
                domainId, hoerernummer, titelnummern);
    }

}

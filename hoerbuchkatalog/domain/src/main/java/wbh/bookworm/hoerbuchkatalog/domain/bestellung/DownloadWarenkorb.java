/*
 * Copyright (C) 2011-2018 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchkatalog.domain.bestellung;

import wbh.bookworm.hoerbuchkatalog.domain.hoerer.Hoerernummer;
import wbh.bookworm.hoerbuchkatalog.domain.katalog.AghNummer;
import wbh.bookworm.hoerbuchkatalog.domain.katalog.Titelnummer;
import wbh.bookworm.platform.ddd.event.DomainEventPublisher;
import wbh.bookworm.platform.ddd.event.DomainEventSubscriber;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;

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
        subscribeToDomainEvents();
    }

    private void subscribeToDomainEvents() {
        DomainEventPublisher.global().subscribe(
                new DomainEventSubscriber<>(BestellungAbgeschickt.class) {
                    @Override
                    public void handleEvent(final BestellungAbgeschickt domainEvent) {
                        logger.info("Was sehen meine entzündeten... {}", domainEvent);
                    }
                });
    }

    @Override
    public void hinzufuegen(final Titelnummer titelnummer) {
        super.hinzufuegen(titelnummer);
        DomainEventPublisher.global()
                .publish(new DownloadInDenWarenkorbGelegt(hoerernummer, titelnummer));
    }

    @Override
    public void entfernen(final Titelnummer titelnummer) {
        super.entfernen(titelnummer);
        DomainEventPublisher.global()
                .publish(new DownloadAusDemWarenkorbEntfernt(hoerernummer, titelnummer));
    }

    @Override
    public void bestellen() {
        logger.info("");
        /* TODO titelnummern -> aghNummern */final Set<AghNummer> aghNummern = null;
        DomainEventPublisher.global()
                .publish(new HoerbuecherAlsDownloadBestellt(hoerernummer, aghNummern));
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

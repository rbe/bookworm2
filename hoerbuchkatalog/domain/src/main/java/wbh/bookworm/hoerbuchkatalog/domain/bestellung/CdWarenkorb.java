/*
 * Copyright (C) 2011-2018 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchkatalog.domain.bestellung;

import wbh.bookworm.hoerbuchkatalog.domain.hoerer.Hoerernummer;
import wbh.bookworm.hoerbuchkatalog.domain.katalog.CdInDenWarenkorbGelegt;
import wbh.bookworm.hoerbuchkatalog.domain.katalog.Titelnummer;
import wbh.bookworm.platform.ddd.event.DomainEventPublisher;
import wbh.bookworm.platform.ddd.event.DomainEventSubscriber;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Set;
import java.util.TreeSet;

public final class CdWarenkorb extends Warenkorb {

    private static final long serialVersionUID = -1L;

    public CdWarenkorb(final Hoerernummer hoerernummer) {
        super(hoerernummer, new TreeSet<>());
        subscribeToDomainEvents();
    }

    @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
    public CdWarenkorb(final @JsonProperty("domainId") Hoerernummer hoerernummer,
                       final @JsonProperty("titelnummern") Set<Titelnummer> titelnummern) {
        super(hoerernummer, titelnummern);
        subscribeToDomainEvents();
    }

    private void subscribeToDomainEvents() {
        DomainEventPublisher.global().subscribe(
                new DomainEventSubscriber<>(CdInDenWarenkorbGelegt.class) {
                    @Override
                    public void handleEvent(final CdInDenWarenkorbGelegt domainEvent) {
                        logger.info("Was sehen meine entzündeten... {}", domainEvent.getTitelnummer());
                    }
                });
    }

    @Override
    public void hinzufuegen(final Titelnummer titelnummer) {
        super.hinzufuegen(titelnummer);
        DomainEventPublisher.global()
                .publish(new CdInDenWarenkorbGelegt(hoerernummer, titelnummer));
    }

    @Override
    public void entfernen(final Titelnummer titelnummer) {
        super.entfernen(titelnummer);
        DomainEventPublisher.global()
                .publish(new CdAusDemWarenkorbEntfernt(hoerernummer, titelnummer));
    }

    @Override
    public void bestellen() {
        logger.info("");
        DomainEventPublisher.global()
                .publish(new HoerbuecherAlsCdBestellt(hoerernummer, titelnummern));
    }

}

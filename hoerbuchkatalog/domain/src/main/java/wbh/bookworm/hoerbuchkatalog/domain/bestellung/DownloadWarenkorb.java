/*
 * Copyright (C) 2011-2018 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchkatalog.domain.bestellung;

import wbh.bookworm.hoerbuchkatalog.domain.hoerer.Hoerernummer;
import wbh.bookworm.hoerbuchkatalog.domain.katalog.AghNummer;
import wbh.bookworm.hoerbuchkatalog.domain.katalog.DownloadInDenWarenkorbGelegt;
import wbh.bookworm.hoerbuchkatalog.domain.katalog.Titelnummer;
import wbh.bookworm.platform.ddd.event.DomainEventPublisher;
import wbh.bookworm.platform.ddd.event.DomainEventSubscriber;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Set;
import java.util.TreeSet;

public final class DownloadWarenkorb extends Warenkorb {

    private static final long serialVersionUID = -1L;

    @JsonIgnore
    private int bestellungenMonat;

    public DownloadWarenkorb(final Hoerernummer hoerernummer) {
        super(hoerernummer, new TreeSet<>());
        this.bestellungenMonat = 0;
    }

    @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
    public DownloadWarenkorb(final @JsonProperty("domainId") Hoerernummer hoerernummer,
                             final @JsonProperty("titelnummern") Set<Titelnummer> titelnummern) {
        this(hoerernummer, titelnummern, 0);
    }

    public DownloadWarenkorb(final Hoerernummer hoerernummer,
                             final Set<Titelnummer> titelnummern,
                             final int bestellungenMonat) {
        super(hoerernummer, titelnummern);
        this.bestellungenMonat = bestellungenMonat;
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

    @JsonIgnore
    public boolean isMaxDownloadsProTagErreicht() {
        return super.getAnzahl() >= 4;// TODO zzgl. bereits getätigte Bestellungen von heute
    }

    @JsonIgnore
    public boolean isMaxDownloadsProMonatErreicht() {
        return bestellungenMonat >= 10;
    }

    @JsonIgnore
    public boolean isMaxDownloadsErreicht() {
        return isMaxDownloadsProTagErreicht() || isMaxDownloadsProMonatErreicht();
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
        /* TODO titelnummern -> aghNummern */
        final Set<AghNummer> aghNummern = null;
        DomainEventPublisher.global()
                .publish(new HoerbuecherAlsDownloadBestellt(hoerernummer, aghNummern));
    }

}

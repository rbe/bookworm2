/*
 * Copyright (C) 2011-2018 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchkatalog.app.bestellung;

import wbh.bookworm.hoerbuchkatalog.app.katalog.HoerbuchkatalogService;
import wbh.bookworm.hoerbuchkatalog.domain.bestellung.Bestellung;
import wbh.bookworm.hoerbuchkatalog.domain.bestellung.BestellungAbgeschickt;
import wbh.bookworm.hoerbuchkatalog.domain.bestellung.BestellungId;
import wbh.bookworm.hoerbuchkatalog.domain.bestellung.CdAusDemWarenkorbEntfernt;
import wbh.bookworm.hoerbuchkatalog.domain.bestellung.CdInDenWarenkorbGelegt;
import wbh.bookworm.hoerbuchkatalog.domain.bestellung.CdWarenkorb;
import wbh.bookworm.hoerbuchkatalog.domain.bestellung.DownloadInDenWarenkorbGelegt;
import wbh.bookworm.hoerbuchkatalog.domain.bestellung.DownloadWarenkorb;
import wbh.bookworm.hoerbuchkatalog.domain.bestellung.WarenkorbId;
import wbh.bookworm.hoerbuchkatalog.domain.hoerer.Hoerernummer;
import wbh.bookworm.hoerbuchkatalog.domain.katalog.Titelnummer;
import wbh.bookworm.hoerbuchkatalog.repository.bestellung.BestellungRepository;
import wbh.bookworm.hoerbuchkatalog.repository.bestellung.WarenkorbRepository;
import wbh.bookworm.platform.ddd.event.DomainEventPublisher;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class BestellungService {

    private static final Logger LOGGER = LoggerFactory.getLogger(BestellungService.class);

    private final HoerbuchkatalogService hoerbuchkatalogService;

    private final WarenkorbRepository warenkorbRepository;

    private final BestellungRepository bestellungRepository;

    @Autowired
    public BestellungService(final HoerbuchkatalogService hoerbuchkatalogService,
                             final WarenkorbRepository warenkorbRepository,
                             final BestellungRepository bestellungRepository) {
        this.hoerbuchkatalogService = hoerbuchkatalogService;
        this.warenkorbRepository = warenkorbRepository;
        this.bestellungRepository = bestellungRepository;
    }

    public CdWarenkorb cdWarenkorbKopie(final Hoerernummer hoerernummer) {
        return new CdWarenkorb(cdWarenkorb(hoerernummer));
    }

    private CdWarenkorb cdWarenkorb(final Hoerernummer hoerernummer) {
        return warenkorbRepository.loadCdWarenkorb(hoerernummer)
                .orElseGet(() -> warenkorbRepository.cdWarenkorbErstellen(hoerernummer));
    }

    /**
     * Command
     */
    public void inDenCdWarenkorb(final Hoerernummer hoerernummer,
                                 final Titelnummer titelnummer) {
        if (hoerbuchkatalogService.hoerbuchVorhanden(hoerernummer, titelnummer)) {
            final CdWarenkorb cdWarenkorb = cdWarenkorb(hoerernummer);
            cdWarenkorb.hinzufuegen(titelnummer);
            warenkorbRepository.save(cdWarenkorb);
            DomainEventPublisher.global()
                    .publish(new CdInDenWarenkorbGelegt(hoerernummer, titelnummer));
        } else {
            LOGGER.error("Unbekanntes Hörbuch #{} wurde nicht für Hörer {} in den CD-Warenkorb gelegt",
                    titelnummer, hoerernummer);
        }
    }

    public boolean imCdWarenkorbEnthalten(final Hoerernummer hoerernummer,
                                          final Titelnummer titelnummer) {
        return cdWarenkorb(hoerernummer).enthalten(titelnummer);
    }

    public int anzahlCdHoerbuecher(final Hoerernummer hoerernummer) {
        return cdWarenkorb(hoerernummer).getAnzahl();
    }

    /**
     * Command
     */
    public void ausDemCdWarenkorbEntfernen(final Hoerernummer hoerernummer,
                                           final Titelnummer titelnummer) {
        if (hoerbuchkatalogService.hoerbuchVorhanden(hoerernummer, titelnummer)) {
            final CdWarenkorb cdWarenkorb = cdWarenkorb(hoerernummer);
            cdWarenkorb.entfernen(titelnummer);
            warenkorbRepository.save(cdWarenkorb);
            DomainEventPublisher.global()
                    .publish(new CdAusDemWarenkorbEntfernt(hoerernummer, titelnummer));
        } else {
            LOGGER.error("Unbekanntes Hörbuch #{} wurde nicht für Hörer {} aus dem CD-Warenkorb entfernt",
                    titelnummer, hoerernummer);
        }
    }

    public DownloadWarenkorb downloadWarenkorbKopie(final Hoerernummer hoerernummer) {
        return new DownloadWarenkorb(downloadWarenkorb(hoerernummer));
    }

    private DownloadWarenkorb downloadWarenkorb(final Hoerernummer hoerernummer) {
        return warenkorbRepository.loadDownloadWarenkorb(hoerernummer)
                .orElseGet(() -> warenkorbRepository.downloadWarenkorbErstellen(hoerernummer));
    }

    /**
     * Command
     */
    public void inDenDownloadWarenkorb(final Hoerernummer hoerernummer,
                                       final Titelnummer titelnummer) {
        if (hoerbuchkatalogService.hoerbuchVorhanden(hoerernummer, titelnummer)) {
            final DownloadWarenkorb downloadWarenkorb = downloadWarenkorb(hoerernummer);
            downloadWarenkorb.hinzufuegen(titelnummer);
            warenkorbRepository.save(downloadWarenkorb);
            DomainEventPublisher.global()
                    .publish(new DownloadInDenWarenkorbGelegt(hoerernummer, titelnummer));
        } else {
            LOGGER.error("Unbekanntes Hörbuch #{} wurde nicht für Hörer {} in den Download-Warenkorb gelegt",
                    titelnummer, hoerernummer);
        }
    }

    public boolean imDownloadWarenkorbEnthalten(final Hoerernummer hoerernummer,
                                                final Titelnummer titelnummer) {
        return downloadWarenkorb(hoerernummer).enthalten(titelnummer);
    }

    /**
     * Command
     */
    public void ausDemDownloadWarenkorbEntfernen(final Hoerernummer hoerernummer,
                                                 final Titelnummer titelnummer) {
        if (hoerbuchkatalogService.hoerbuchVorhanden(hoerernummer, titelnummer)) {
            final DownloadWarenkorb downloadWarenkorb = downloadWarenkorb(hoerernummer);
            downloadWarenkorb.entfernen(titelnummer);
            warenkorbRepository.save(downloadWarenkorb);
        } else {
            LOGGER.error("Unbekanntes Hörbuch #{} wurde nicht für Hörer {} aus dem Download-Warenkorb entfernt",
                    titelnummer, hoerernummer);
        }
    }

    public int anzahlDownloadHoerbuecher(final Hoerernummer hoerernummer) {
        return downloadWarenkorb(hoerernummer).getAnzahl();
    }

    public int anzahlHoerbuecher(final Hoerernummer hoerernummer) {
        return anzahlCdHoerbuecher(hoerernummer) + anzahlDownloadHoerbuecher(hoerernummer);
    }

    /**
     * Command
     */
    public Optional<BestellungId> bestellungAufgeben(final Hoerernummer hoerernummer,
                                                     final String hoerername, final String hoereremail,
                                                     final String bemerkung,
                                                     final Boolean bestellkarteMischen, final Boolean alteBestellkarteLoeschen,
                                                     final WarenkorbId cdWarenkorbId, final WarenkorbId downloadWarenkorbId) {
        LOGGER.trace("Bestellung {} für {} wird aufgegeben!", this, hoerernummer);
        final Bestellung bestellung = bestellungRepository.erstellen(hoerernummer,
                hoerername, hoereremail,
                bemerkung,
                bestellkarteMischen, alteBestellkarteLoeschen,
                cdWarenkorbId, downloadWarenkorbId);
        final BestellungId bestellungId = bestellungRepository.save(bestellung);
        if (null != bestellungId) {
            cdWarenkorbBestellen(hoerernummer);
            downloadWarenkorbBestellen(hoerernummer);
            DomainEventPublisher.global()
                    .publish(new BestellungAbgeschickt(bestellungId,
                            hoerernummer, cdWarenkorbId, downloadWarenkorbId));
            LOGGER.info("Bestellung {} für Hörer {} wurde erfolgreich aufgegeben!", this, hoerernummer);
            return Optional.of(bestellungId);
        } else {
            LOGGER.error("Bestellung {} für Hörer {} konnte nicht aufgegeben werden; Speicherung fehlgeschlagen",
                    this, hoerernummer);
            return Optional.empty();
        }
    }

    private void downloadWarenkorbBestellen(final Hoerernummer hoerernummer) {
        final DownloadWarenkorb downloadWarenkorb = downloadWarenkorb(hoerernummer);
        downloadWarenkorb.bestellen();
        downloadWarenkorb.leeren();
        warenkorbRepository.save(downloadWarenkorb);
    }

    private void cdWarenkorbBestellen(final Hoerernummer hoerernummer) {
        final CdWarenkorb cdWarenkorb = cdWarenkorb(hoerernummer);
        cdWarenkorb.bestellen();
        cdWarenkorb.leeren();
        warenkorbRepository.save(cdWarenkorb);
    }

    public boolean isMaxDownloadsProTagErreicht() {
        return false;
    }

    public boolean isMaxDownloadsProMonatErreicht() {
        return false;
    }

}

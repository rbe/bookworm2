/*
 * Copyright (C) 2011-2018 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchkatalog.app.bestellung;

import wbh.bookworm.hoerbuchkatalog.app.katalog.HoerbuchkatalogService;
import wbh.bookworm.hoerbuchkatalog.domain.bestellung.Bestellung;
import wbh.bookworm.hoerbuchkatalog.domain.bestellung.BestellungId;
import wbh.bookworm.hoerbuchkatalog.domain.bestellung.CdWarenkorb;
import wbh.bookworm.hoerbuchkatalog.domain.bestellung.DownloadWarenkorb;
import wbh.bookworm.hoerbuchkatalog.domain.hoerer.Hoerernummer;
import wbh.bookworm.hoerbuchkatalog.domain.katalog.Titelnummer;
import wbh.bookworm.hoerbuchkatalog.repository.bestellung.BestellungRepository;
import wbh.bookworm.hoerbuchkatalog.repository.bestellung.WarenkorbRepository;

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
            /*warenkorbRepository.save(cdWarenkorb);
            DomainEventPublisher.global()
                    .publish(new CdInDenWarenkorbGelegt(hoerernummer, titelnummer));*/
        } else {
            LOGGER.error("Unbekanntes Hörbuch #{} wurde nicht für Hörer {} in den CD-Warenkorb gelegt",
                    titelnummer, hoerernummer);
        }
    }

    public boolean imCdWarenkorbEnthalten(final Hoerernummer hoerernummer,
                                          final Titelnummer titelnummer) {
        return cdWarenkorb(hoerernummer).enthalten(titelnummer);
    }

    public int anzahlHoerbuecherAlsCd(final Hoerernummer hoerernummer) {
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
            /*warenkorbRepository.save(cdWarenkorb);
            DomainEventPublisher.global()
                    .publish(new CdAusDemWarenkorbEntfernt(hoerernummer, titelnummer));*/
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
        } else {
            LOGGER.error("Unbekanntes Hörbuch #{} wurde nicht für Hörer {} aus dem Download-Warenkorb entfernt",
                    titelnummer, hoerernummer);
        }
    }

    public int anzahlHoerbuecherAlsDownload(final Hoerernummer hoerernummer) {
        return downloadWarenkorb(hoerernummer).getAnzahl();
    }

    public int anzahlHoerbuecher(final Hoerernummer hoerernummer) {
        return anzahlHoerbuecherAlsCd(hoerernummer) + anzahlHoerbuecherAlsDownload(hoerernummer);
    }

    /**
     * Command
     */
    public Optional<BestellungId> bestellungAufgeben(final Hoerernummer hoerernummer,
                                                     final String hoerername, final String hoereremail,
                                                     final String bemerkung,
                                                     final Boolean bestellkarteMischen,
                                                     final Boolean alteBestellkarteLoeschen) {
        LOGGER.trace("Bestellung {} für Hörer {} wird aufgegeben", this, hoerernummer);
        final CdWarenkorb cdWarenkorb = cdWarenkorb(hoerernummer);
        final DownloadWarenkorb downloadWarenkorb = downloadWarenkorb(hoerernummer);
        final Bestellung bestellung = bestellungRepository.erstellen(hoerernummer,
                hoerername, hoereremail,
                bemerkung,
                bestellkarteMischen, alteBestellkarteLoeschen,
                cdWarenkorb.getTitelnummern(), downloadWarenkorb.getTitelnummern());
        cdWarenkorb.leeren();
        downloadWarenkorb.leeren();
        bestellung.aufgeben();
        LOGGER.info("Bestellung {} für Hörer {} wurde erfolgreich aufgegeben!", bestellung, hoerernummer);
        return Optional.of(bestellung.getDomainId());
    }

    public boolean isMaxDownloadsProTagErreicht(final Hoerernummer hoerernummer) {
        return (bestellungRepository.countAlleDownloadsVonHeute(hoerernummer) +
                downloadWarenkorb(hoerernummer).getAnzahl()) >= 4;
    }

    public boolean isMaxDownloadsProMonatErreicht(final Hoerernummer hoerernummer) {
        return (bestellungRepository.countAlleDownloadsInDiesemMonat(hoerernummer) +
                downloadWarenkorb(hoerernummer).getAnzahl()) >= 10;
    }

}

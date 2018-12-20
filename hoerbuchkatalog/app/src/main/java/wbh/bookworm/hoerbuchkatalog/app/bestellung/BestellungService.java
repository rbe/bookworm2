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
import wbh.bookworm.hoerbuchkatalog.domain.bestellung.WarenkorbId;
import wbh.bookworm.hoerbuchkatalog.domain.hoerer.HoererEmail;
import wbh.bookworm.hoerbuchkatalog.domain.hoerer.Hoerername;
import wbh.bookworm.hoerbuchkatalog.domain.hoerer.Hoerernummer;
import wbh.bookworm.hoerbuchkatalog.domain.katalog.Titelnummer;
import wbh.bookworm.hoerbuchkatalog.repository.bestellung.BestellungRepository;
import wbh.bookworm.hoerbuchkatalog.repository.bestellung.WarenkorbRepository;

import aoc.ddd.repository.QueryPredicate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
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

    //
    // CD Warenkorb
    //

    public CdWarenkorb cdWarenkorbKopie(final BestellungSessionId bestellungSessionId) {
        final WarenkorbId warenkorbId = warenkorbIdFrom(bestellungSessionId, "CD");
        final Hoerernummer hoerernummer = new Hoerernummer(bestellungSessionId.getHoerernummer());
        return new CdWarenkorb(cdWarenkorb(hoerernummer, warenkorbId));
    }

    private CdWarenkorb cdWarenkorb(final Hoerernummer hoerernummer, final WarenkorbId warenkorbId) {
        return warenkorbRepository.loadCdWarenkorb(warenkorbId)
                .orElseGet(() -> warenkorbRepository.cdWarenkorbErstellen(warenkorbId, hoerernummer));
    }

    /**
     * Command
     */
    public void inDenCdWarenkorb(final BestellungSessionId bestellungSessionId,
                                 final Titelnummer titelnummer) {
        final WarenkorbId warenkorbId = warenkorbIdFrom(bestellungSessionId, "CD");
        final Hoerernummer hoerernummer = new Hoerernummer(bestellungSessionId.getHoerernummer());
        if (hoerbuchkatalogService.hoerbuchVorhanden(hoerernummer, titelnummer)) {
            final CdWarenkorb cdWarenkorb = cdWarenkorb(hoerernummer, warenkorbId);
            cdWarenkorb.hinzufuegen(titelnummer);
        } else {
            LOGGER.error("Unbekanntes Hörbuch #{} wurde nicht für Hörer {} in den CD-Warenkorb gelegt",
                    titelnummer, bestellungSessionId.getHoerernummer());
        }
    }

    public boolean imCdWarenkorbEnthalten(final BestellungSessionId bestellungSessionId,
                                          final Titelnummer titelnummer) {
        final WarenkorbId warenkorbId = warenkorbIdFrom(bestellungSessionId, "CD");
        final Hoerernummer hoerernummer = new Hoerernummer(bestellungSessionId.getHoerernummer());
        return cdWarenkorb(hoerernummer, warenkorbId).enthalten(titelnummer);
    }

    public int anzahlHoerbuecherAlsCd(final BestellungSessionId bestellungSessionId) {
        final WarenkorbId warenkorbId = warenkorbIdFrom(bestellungSessionId, "CD");
        final Hoerernummer hoerernummer = new Hoerernummer(bestellungSessionId.getHoerernummer());
        return cdWarenkorb(hoerernummer, warenkorbId).getAnzahl();
    }

    /**
     * Command
     */
    public void ausDemCdWarenkorbEntfernen(final BestellungSessionId bestellungSessionId,
                                           final Titelnummer titelnummer) {
        final WarenkorbId warenkorbId = warenkorbIdFrom(bestellungSessionId, "CD");
        final Hoerernummer hoerernummer = new Hoerernummer(bestellungSessionId.getHoerernummer());
        if (hoerbuchkatalogService.hoerbuchVorhanden(hoerernummer, titelnummer)) {
            final CdWarenkorb cdWarenkorb = cdWarenkorb(hoerernummer, warenkorbId);
            cdWarenkorb.entfernen(titelnummer);
        } else {
            LOGGER.error("Unbekanntes Hörbuch #{} wurde nicht für Hörer {} aus dem CD-Warenkorb entfernt",
                    titelnummer, hoerernummer);
        }
    }

    //
    // Download Warenkorb
    //

    public DownloadWarenkorb downloadWarenkorbKopie(final BestellungSessionId bestellungSessionId) {
        final WarenkorbId warenkorbId = warenkorbIdFrom(bestellungSessionId, "Download");
        final Hoerernummer hoerernummer = new Hoerernummer(bestellungSessionId.getHoerernummer());
        return new DownloadWarenkorb(downloadWarenkorb(hoerernummer, warenkorbId));
    }

    private DownloadWarenkorb downloadWarenkorb(final Hoerernummer hoerernummer, final WarenkorbId warenkorbId) {
        return warenkorbRepository.loadDownloadWarenkorb(warenkorbId)
                .orElseGet(() -> warenkorbRepository.downloadWarenkorbErstellen(warenkorbId, hoerernummer));
    }

    /**
     * Command
     */
    public void inDenDownloadWarenkorb(final BestellungSessionId bestellungSessionId,
                                       final Titelnummer titelnummer) {
        final WarenkorbId warenkorbId = warenkorbIdFrom(bestellungSessionId, "Download");
        final Hoerernummer hoerernummer = new Hoerernummer(bestellungSessionId.getHoerernummer());
        if (hoerbuchkatalogService.hoerbuchVorhanden(hoerernummer, titelnummer)) {
            final DownloadWarenkorb downloadWarenkorb = downloadWarenkorb(hoerernummer, warenkorbId);
            downloadWarenkorb.hinzufuegen(titelnummer);
        } else {
            LOGGER.error("Unbekanntes Hörbuch #{} wurde nicht für Hörer {} in den Download-Warenkorb gelegt",
                    titelnummer, hoerernummer);
        }
    }

    public boolean imDownloadWarenkorbEnthalten(final BestellungSessionId bestellungSessionId,
                                                final Titelnummer titelnummer) {
        final WarenkorbId warenkorbId = warenkorbIdFrom(bestellungSessionId, "Download");
        final Hoerernummer hoerernummer = new Hoerernummer(bestellungSessionId.getHoerernummer());
        return downloadWarenkorb(hoerernummer, warenkorbId).enthalten(titelnummer);
    }

    /**
     * Command
     */
    public void ausDemDownloadWarenkorbEntfernen(final BestellungSessionId bestellungSessionId,
                                                 final Titelnummer titelnummer) {
        final WarenkorbId warenkorbId = warenkorbIdFrom(bestellungSessionId, "Download");
        final Hoerernummer hoerernummer = new Hoerernummer(bestellungSessionId.getHoerernummer());
        if (hoerbuchkatalogService.hoerbuchVorhanden(hoerernummer, titelnummer)) {
            final DownloadWarenkorb downloadWarenkorb = downloadWarenkorb(hoerernummer, warenkorbId);
            downloadWarenkorb.entfernen(titelnummer);
        } else {
            LOGGER.error("Unbekanntes Hörbuch #{} wurde nicht für Hörer {} aus dem Download-Warenkorb entfernt",
                    titelnummer, hoerernummer);
        }
    }

    public int anzahlHoerbuecherAlsDownload(final BestellungSessionId bestellungSessionId) {
        final WarenkorbId warenkorbId = warenkorbIdFrom(bestellungSessionId, "Download");
        final Hoerernummer hoerernummer = new Hoerernummer(bestellungSessionId.getHoerernummer());
        return downloadWarenkorb(hoerernummer, warenkorbId).getAnzahl();
    }

    public boolean isMaxDownloadsProTagErreicht(final BestellungSessionId bestellungSessionId) {
        final WarenkorbId warenkorbId = warenkorbIdFrom(bestellungSessionId, "Download");
        final Hoerernummer hoerernummer = new Hoerernummer(bestellungSessionId.getHoerernummer());
        return (bestellungRepository.countAlleDownloadsVonHeute(hoerernummer) +
                downloadWarenkorb(hoerernummer, warenkorbId).getAnzahl()) >= /* TODO Konfigurieren */5;
    }

    public boolean isMaxDownloadsProMonatErreicht(final BestellungSessionId bestellungSessionId) {
        final WarenkorbId warenkorbId = warenkorbIdFrom(bestellungSessionId, "Download");
        final Hoerernummer hoerernummer = new Hoerernummer(bestellungSessionId.getHoerernummer());
        return (bestellungRepository.countAlleDownloadsInDiesemMonat(hoerernummer) +
                downloadWarenkorb(hoerernummer, warenkorbId).getAnzahl()) >= /* TODO Konfigurieren */10;
    }

    //
    // Warenkorb
    //

    /**
     * Hoerernummer aus {@link BestellungSessionId} erzeugen.
     * Bei unbekannten Hörern wird die SessionId angehangen,
     * bei bekannten die Hörernummer verwendet.
     */
    private WarenkorbId warenkorbIdFrom(final BestellungSessionId bestellungSessionId,
                                        final String diskriminator) {
        final Hoerernummer hoerernummer = new Hoerernummer(bestellungSessionId.getHoerernummer());
        if (Hoerernummer.UNBEKANNT.equals(hoerernummer)) {
            return new WarenkorbId(String.format("%s-%s-%s",
                    hoerernummer.getValue(), bestellungSessionId.getSessionId(), diskriminator));
        } else {
            return new WarenkorbId(String.format("%s-%s",
                    hoerernummer.getValue(), diskriminator));
        }
    }

    public int anzahlHoerbuecher(final BestellungSessionId bestellungSessionId) {
        return anzahlHoerbuecherAlsCd(bestellungSessionId) + anzahlHoerbuecherAlsDownload(bestellungSessionId);
    }

    //
    // Bestellung
    //

    /**
     * Command
     */
    public Optional<BestellungId> bestellungAufgeben(/* TODO Hoerer */final BestellungSessionId bestellungSessionId,
                                                                      final Hoerername hoerername, final HoererEmail hoereremail,
                                                                      final String bemerkung,
                                                                      final Boolean bestellkarteMischen,
                                                                      final Boolean alteBestellkarteLoeschen) {
        final Hoerernummer hoerernummer = new Hoerernummer(bestellungSessionId.getHoerernummer());
        LOGGER.trace("Bestellung {} für Hörer {} wird aufgegeben", this, hoerernummer);
        final WarenkorbId cdWarenkorbId = warenkorbIdFrom(bestellungSessionId, "CD");
        final CdWarenkorb cdWarenkorb = cdWarenkorb(hoerernummer, cdWarenkorbId);
        final WarenkorbId downloadWarenkorbId = warenkorbIdFrom(bestellungSessionId, "Download");
        final DownloadWarenkorb downloadWarenkorb = downloadWarenkorb(hoerernummer, downloadWarenkorbId);
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

    public long anzahlBestellungen(final Hoerernummer hoerernummer) {
        return bestellungRepository
                .find(QueryPredicate.Equals.of("hoerernummer", hoerernummer.getValue()))
                .orElseGet(Collections::emptySet)
                .size();
    }

}

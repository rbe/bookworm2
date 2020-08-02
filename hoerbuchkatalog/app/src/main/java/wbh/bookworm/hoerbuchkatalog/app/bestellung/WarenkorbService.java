/*
 * Copyright (C) 2011-2019 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchkatalog.app.bestellung;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import wbh.bookworm.hoerbuchkatalog.app.katalog.HoerbuchkatalogService;
import wbh.bookworm.hoerbuchkatalog.domain.bestellung.CdWarenkorb;
import wbh.bookworm.hoerbuchkatalog.domain.bestellung.DownloadWarenkorb;
import wbh.bookworm.hoerbuchkatalog.domain.bestellung.WarenkorbId;
import wbh.bookworm.hoerbuchkatalog.repository.bestellung.WarenkorbRepository;
import wbh.bookworm.hoerbuchkatalog.repository.lieferung.DownloadsRepository;
import wbh.bookworm.shared.domain.hoerbuch.Titelnummer;
import wbh.bookworm.shared.domain.mandant.Hoerernummer;

// TODO Peristenz für Warenkörbe nicht notwendig -> WK nur pro Session gültig, Session persistieren
@Service
public class WarenkorbService {

    private static final Logger LOGGER = LoggerFactory.getLogger(WarenkorbService.class);

    private static final String DOWNLOAD = "Download";

    public static final String CD = "CD";

    private final HoerbuchkatalogService hoerbuchkatalogService;

    private final WarenkorbRepository warenkorbRepository;

    private final DownloadsRepository downloadsRepository;

    @Autowired
    public WarenkorbService(final HoerbuchkatalogService hoerbuchkatalogService,
                            final WarenkorbRepository warenkorbRepository,
                            final DownloadsRepository downloadsRepository) {
        this.hoerbuchkatalogService = hoerbuchkatalogService;
        this.warenkorbRepository = warenkorbRepository;
        this.downloadsRepository = downloadsRepository;
    }

    //
    // CD Warenkorb
    //

    public CdWarenkorb cdWarenkorbKopie(final BestellungSessionId bestellungSessionId,
                                        final Hoerernummer hoerernummer) {
        final WarenkorbId warenkorbId = warenkorbIdFrom(bestellungSessionId, CD);
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
                                 final Hoerernummer hoerernummer,
                                 final Titelnummer titelnummer) {
        final WarenkorbId warenkorbId = warenkorbIdFrom(bestellungSessionId, CD);
        if (hoerbuchkatalogService.hoerbuchVorhanden(hoerernummer, titelnummer)) {
            final CdWarenkorb cdWarenkorb = cdWarenkorb(hoerernummer, warenkorbId);
            cdWarenkorb.hinzufuegen(titelnummer);
        } else {
            LOGGER.error("Unbekanntes Hörbuch #{} wurde nicht für Hörer {} in den CD-Warenkorb gelegt",
                    titelnummer, hoerernummer);
        }
    }

    public boolean imCdWarenkorbEnthalten(final BestellungSessionId bestellungSessionId,
                                          final Hoerernummer hoerernummer,
                                          final Titelnummer titelnummer) {
        final WarenkorbId warenkorbId = warenkorbIdFrom(bestellungSessionId, CD);
        return cdWarenkorb(hoerernummer, warenkorbId).enthalten(titelnummer);
    }

    public int anzahlHoerbuecherAlsCd(final BestellungSessionId bestellungSessionId,
                                      final Hoerernummer hoerernummer) {
        final WarenkorbId warenkorbId = warenkorbIdFrom(bestellungSessionId, CD);
        return cdWarenkorb(hoerernummer, warenkorbId).getAnzahl();
    }

    /**
     * Command
     */
    public void ausDemCdWarenkorbEntfernen(final BestellungSessionId bestellungSessionId,
                                           final Hoerernummer hoerernummer,
                                           final Titelnummer titelnummer) {
        final WarenkorbId warenkorbId = warenkorbIdFrom(bestellungSessionId, CD);
        if (hoerbuchkatalogService.hoerbuchVorhanden(hoerernummer, titelnummer)) {
            final CdWarenkorb cdWarenkorb = cdWarenkorb(hoerernummer, warenkorbId);
            cdWarenkorb.entfernen(titelnummer);
        } else {
            LOGGER.error("Unbekanntes Hörbuch #{} wurde nicht für Hörer {} aus dem CD-Warenkorb entfernt",
                    titelnummer, hoerernummer);
        }
    }

    public void cdWarenkorbLoeschen(final BestellungSessionId bestellungSessionId) {
        final WarenkorbId warenkorbId = warenkorbIdFrom(bestellungSessionId, CD);
        warenkorbRepository.delete(warenkorbId);
    }

    //
    // Download Warenkorb
    //

    public DownloadWarenkorb downloadWarenkorbKopie(final BestellungSessionId bestellungSessionId,
                                                    final Hoerernummer hoerernummer) {
        final WarenkorbId warenkorbId = warenkorbIdFrom(bestellungSessionId, DOWNLOAD);
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
                                       final Hoerernummer hoerernummer,
                                       final Titelnummer titelnummer) {
        final WarenkorbId warenkorbId = warenkorbIdFrom(bestellungSessionId, DOWNLOAD);
        if (hoerbuchkatalogService.hoerbuchVorhanden(hoerernummer, titelnummer)) {
            final DownloadWarenkorb downloadWarenkorb = downloadWarenkorb(hoerernummer, warenkorbId);
            downloadWarenkorb.hinzufuegen(titelnummer);
        } else {
            LOGGER.error("Unbekanntes Hörbuch #{} wurde nicht für Hörer {} in den Download-Warenkorb gelegt",
                    titelnummer, hoerernummer);
        }
    }

    public boolean imDownloadWarenkorbEnthalten(final BestellungSessionId bestellungSessionId,
                                                final Hoerernummer hoerernummer,
                                                final Titelnummer titelnummer) {
        final WarenkorbId warenkorbId = warenkorbIdFrom(bestellungSessionId, DOWNLOAD);
        return downloadWarenkorb(hoerernummer, warenkorbId).enthalten(titelnummer);
    }

    /**
     * Command
     */
    public void ausDemDownloadWarenkorbEntfernen(final BestellungSessionId bestellungSessionId,
                                                 final Hoerernummer hoerernummer,
                                                 final Titelnummer titelnummer) {
        final WarenkorbId warenkorbId = warenkorbIdFrom(bestellungSessionId, DOWNLOAD);
        if (hoerbuchkatalogService.hoerbuchVorhanden(hoerernummer, titelnummer)) {
            final DownloadWarenkorb downloadWarenkorb = downloadWarenkorb(hoerernummer, warenkorbId);
            downloadWarenkorb.entfernen(titelnummer);
        } else {
            LOGGER.error("Unbekanntes Hörbuch {} wurde nicht für Hörer {} aus dem Download-Warenkorb entfernt",
                    titelnummer, hoerernummer);
        }
    }

    public int anzahlHoerbuecherAlsDownload(final BestellungSessionId bestellungSessionId,
                                            final Hoerernummer hoerernummer) {
        final WarenkorbId warenkorbId = warenkorbIdFrom(bestellungSessionId, DOWNLOAD);
        return downloadWarenkorb(hoerernummer, warenkorbId).getAnzahl();
    }

    public boolean isMaxDownloadsProTagErreicht(final BestellungSessionId bestellungSessionId,
                                                final Hoerernummer hoerernummer) {
        final WarenkorbId warenkorbId = warenkorbIdFrom(bestellungSessionId, DOWNLOAD);
        final long vonHeute = downloadsRepository.anzahlLieferungenHeute(hoerernummer);
        final int imWarenkorb = downloadWarenkorb(hoerernummer, warenkorbId).getAnzahl();
        return (vonHeute + imWarenkorb) >= /* TODO Konfigurieren */5;
    }

    public boolean isMaxDownloadsProMonatErreicht(final BestellungSessionId bestellungSessionId,
                                                  final Hoerernummer hoerernummer) {
        final WarenkorbId warenkorbId = warenkorbIdFrom(bestellungSessionId, DOWNLOAD);
        final long inDiesemMonat = downloadsRepository.anzahlLieferungenInDiesemMonat(hoerernummer);
        final int imWarenkorb = downloadWarenkorb(hoerernummer, warenkorbId).getAnzahl();
        return (inDiesemMonat + imWarenkorb) >= /* TODO Konfigurieren */10;
    }

    public void downloadWarenkorbLoeschen(final BestellungSessionId bestellungSessionId) {
        final WarenkorbId warenkorbId = warenkorbIdFrom(bestellungSessionId, DOWNLOAD);
        warenkorbRepository.delete(warenkorbId);
    }

    //
    // Warenkorb
    //

    /**
     * Hoerernummer aus {@link BestellungSessionId} erzeugen.
     * Bei unbekannten Hörern wird die SessionId angehangen,
     * bei bekannten die Hörernummer verwendet.
     */
    private static WarenkorbId warenkorbIdFrom(final BestellungSessionId bestellungSessionId,
                                               final String diskriminator) {
        return new WarenkorbId(String.format("%s-%s", bestellungSessionId, diskriminator));
    }

}

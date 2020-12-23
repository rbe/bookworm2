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
import wbh.bookworm.hoerbuchkatalog.domain.bestellung.BestellungSessionId;
import wbh.bookworm.hoerbuchkatalog.domain.bestellung.CdWarenkorb;
import wbh.bookworm.hoerbuchkatalog.domain.bestellung.WarenkorbId;
import wbh.bookworm.hoerbuchkatalog.repository.bestellung.WarenkorbRepository;
import wbh.bookworm.shared.domain.Hoerernummer;
import wbh.bookworm.shared.domain.Titelnummer;

// TODO Peristenz für Warenkörbe nicht notwendig -> WK nur pro Session gültig, Session persistieren
@Service
public class WarenkorbService {

    public static final String CD = "CD";

    private static final Logger LOGGER = LoggerFactory.getLogger(WarenkorbService.class);

    private static final String DOWNLOAD = "Download";

    private final HoerbuchkatalogService hoerbuchkatalogService;

    private final WarenkorbRepository warenkorbRepository;

    @Autowired
    public WarenkorbService(final HoerbuchkatalogService hoerbuchkatalogService,
                            final WarenkorbRepository warenkorbRepository) {
        this.hoerbuchkatalogService = hoerbuchkatalogService;
        this.warenkorbRepository = warenkorbRepository;
    }

    //
    // CD Warenkorb
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
    public boolean inDenCdWarenkorb(final BestellungSessionId bestellungSessionId,
                                    final Hoerernummer hoerernummer,
                                    final Titelnummer titelnummer) {
        final WarenkorbId warenkorbId = warenkorbIdFrom(bestellungSessionId, CD);
        if (hoerbuchkatalogService.hoerbuchVorhanden(hoerernummer, titelnummer)) {
            final CdWarenkorb cdWarenkorb = cdWarenkorb(hoerernummer, warenkorbId);
            cdWarenkorb.hinzufuegen(titelnummer);
            return true;
        } else {
            LOGGER.error("Unbekanntes Hörbuch #{} wurde nicht für Hörer {} in den CD-Warenkorb gelegt",
                    titelnummer, hoerernummer);
            return false;
        }
    }

    public boolean imCdWarenkorbEnthalten(final BestellungSessionId bestellungSessionId,
                                          final Hoerernummer hoerernummer,
                                          final Titelnummer titelnummer) {
        final WarenkorbId warenkorbId = warenkorbIdFrom(bestellungSessionId, CD);
        final CdWarenkorb cdWarenkorb = cdWarenkorb(hoerernummer, warenkorbId);
        return cdWarenkorb.enthalten(titelnummer);
    }

    public int anzahlHoerbuecherAlsCd(final BestellungSessionId bestellungSessionId,
                                      final Hoerernummer hoerernummer) {
        final WarenkorbId warenkorbId = warenkorbIdFrom(bestellungSessionId, CD);
        return cdWarenkorb(hoerernummer, warenkorbId).getAnzahl();
    }

    /**
     * Command
     */
    public boolean ausDemCdWarenkorbEntfernen(final BestellungSessionId bestellungSessionId,
                                              final Hoerernummer hoerernummer,
                                              final Titelnummer titelnummer) {
        final WarenkorbId warenkorbId = warenkorbIdFrom(bestellungSessionId, CD);
        if (hoerbuchkatalogService.hoerbuchVorhanden(hoerernummer, titelnummer)) {
            final CdWarenkorb cdWarenkorb = cdWarenkorb(hoerernummer, warenkorbId);
            cdWarenkorb.entfernen(titelnummer);
            return true;
        } else {
            LOGGER.error("Unbekanntes Hörbuch #{} wurde nicht für Hörer {} aus dem CD-Warenkorb entfernt",
                    titelnummer, hoerernummer);
            return false;
        }
    }

    //
    // Warenkorb
    //

    public void cdWarenkorbLoeschen(final BestellungSessionId bestellungSessionId) {
        final WarenkorbId warenkorbId = warenkorbIdFrom(bestellungSessionId, CD);
        warenkorbRepository.delete(warenkorbId);
    }

}

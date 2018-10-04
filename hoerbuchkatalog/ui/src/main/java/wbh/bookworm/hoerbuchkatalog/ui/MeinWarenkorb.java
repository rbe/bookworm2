/*
 * Copyright (C) 2011-2018 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchkatalog.ui;

import wbh.bookworm.hoerbuchkatalog.app.bestellung.BestellungService;
import wbh.bookworm.hoerbuchkatalog.app.katalog.HoerbuchkatalogService;
import wbh.bookworm.hoerbuchkatalog.domain.bestellung.CdWarenkorb;
import wbh.bookworm.hoerbuchkatalog.domain.bestellung.DownloadWarenkorb;
import wbh.bookworm.hoerbuchkatalog.domain.hoerer.Hoerernummer;
import wbh.bookworm.hoerbuchkatalog.domain.katalog.Titelnummer;
import wbh.bookworm.platform.jsf.ELCacheGroup;
import wbh.bookworm.platform.jsf.ELFunctionCache;
import wbh.bookworm.platform.jsf.ELValueCache;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.SessionScope;

@Component
@SessionScope
class MeinWarenkorb {

    private static final Logger LOGGER = LoggerFactory.getLogger(MeinWarenkorb.class);

    private final Hoerernummer hoerernummer;

    private final HoerbuchkatalogService hoerbuchkatalogService;

    private final BestellungService bestellungService;

    private final ELCacheGroup cdWarenkorbCacheGroup;

    private final ELCacheGroup downloadWarenkorbCacheGroup;

    private final ELValueCache<Integer> cdAnzahlValueCache;

    private final ELValueCache<Integer> downloadAnzahlValueCache;

    private final ELFunctionCache<Titelnummer, Boolean> downloadVerfuegbarValueCache;

    @Autowired
    MeinWarenkorb(final Hoerernummer hoerernummer,
                  final HoerbuchkatalogService hoerbuchkatalogService,
                  final BestellungService bestellungService) {
        LOGGER.trace("Initializing");
        this.hoerernummer = hoerernummer;
        this.hoerbuchkatalogService = hoerbuchkatalogService;
        this.bestellungService = bestellungService;
        // CD Warenkorb
        cdWarenkorbCacheGroup = new ELCacheGroup();
        // Download Warenkorb
        downloadWarenkorbCacheGroup = new ELCacheGroup();
        //
        cdAnzahlValueCache = new ELValueCache<>(0,
                () -> bestellungService.anzahlCdHoerbuecher(hoerernummer));
        downloadAnzahlValueCache = new ELValueCache<>(0,
                () -> bestellungService.anzahlDownloadHoerbuecher(hoerernummer));
        //
        downloadVerfuegbarValueCache = new ELFunctionCache<>(
                (titelnummer) -> hoerbuchkatalogService.hoerbuchDownloadbar(hoerernummer, titelnummer));
    }

    public void leeren() {
        cdAnzahlValueCache.invalidate();
        downloadAnzahlValueCache.invalidate();
    }

    public int getTotaleAnzahl() {
        LOGGER.trace("");
        return cdAnzahlValueCache.get() + downloadAnzahlValueCache.get();
    }

    //
    // CD Warenkorb
    //


    public CdWarenkorb getCd() {
        LOGGER.trace("");
        return bestellungService.cdWarenkorbKopie(hoerernummer);
    }

    public boolean cdEnthalten(final Titelnummer titelnummer) {
        LOGGER.trace("");
        return bestellungService.imCdWarenkorbEnthalten(hoerernummer, titelnummer);
    }

    public void cdHinzufuegen(final Titelnummer titelnummer) {
        LOGGER.trace("");
        cdAnzahlValueCache.invalidate();
        bestellungService.inDenCdWarenkorb(hoerernummer, titelnummer);
    }

    public void cdEntfernen(final Titelnummer titelnummer) {
        LOGGER.trace("");
        cdAnzahlValueCache.invalidate();
        bestellungService.ausDemCdWarenkorbEntfernen(hoerernummer, titelnummer);
    }

    //
    // Download Warenkorb
    //

    public DownloadWarenkorb getDownload() {
        LOGGER.trace("");
        return bestellungService.downloadWarenkorbKopie(hoerernummer);
    }

    public boolean downloadVerfuegbar(final Titelnummer titelnummer) {
        LOGGER.trace("");
        return downloadVerfuegbarValueCache.get(titelnummer);
    }

    public boolean downloadEnthalten(final Titelnummer titelnummer) {
        LOGGER.trace("");
        return bestellungService.imDownloadWarenkorbEnthalten(hoerernummer, titelnummer);
    }

    public void downloadHinzufuegen(final Titelnummer titelnummer) {
        LOGGER.trace("");
        downloadAnzahlValueCache.invalidate();
        bestellungService.inDenDownloadWarenkorb(hoerernummer, titelnummer);
    }

    public void downloadEntfernen(final Titelnummer titelnummer) {
        LOGGER.trace("");
        downloadAnzahlValueCache.invalidate();
        bestellungService.ausDemDownloadWarenkorbEntfernen(hoerernummer, titelnummer);
    }

    public boolean isMaxDownloadsProTagErreicht() {
        LOGGER.trace("");
        return bestellungService.isMaxDownloadsProTagErreicht();
    }

    public boolean isMaxDownloadsProMonatErreicht() {
        LOGGER.trace("");
        return bestellungService.isMaxDownloadsProMonatErreicht();
    }

    //
    // Hörbuch
    //


    public String hoerbuchAutor(final Titelnummer titelnummer) {
        LOGGER.trace("");
        return hoerbuchkatalogService.hole(hoerernummer, titelnummer).getAutor();
    }

    public String hoerbuchTitel(final Titelnummer titelnummer) {
        LOGGER.trace("");
        return hoerbuchkatalogService.hole(hoerernummer, titelnummer).getTitel();
    }

}

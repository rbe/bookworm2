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
import wbh.bookworm.hoerbuchkatalog.domain.katalog.Hoerbuch;
import wbh.bookworm.hoerbuchkatalog.domain.katalog.Titelnummer;

import aoc.jsf.ELFunctionCache;
import aoc.jsf.ELValueCache;
import aoc.jsf.ELValueCacheGroup;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.SessionScope;

@Component
@SessionScope
class MeinWarenkorb {

    private static final Logger LOGGER = LoggerFactory.getLogger(MeinWarenkorb.class);

    //
    // Hörer
    //

    private final Hoerernummer hoerernummer;

    //
    // Hörbuchkatalog
    //

    private final ELFunctionCache<Titelnummer, Hoerbuch> hoerbuchValueCache;

    //
    // Bestellung
    //

    private final BestellungService bestellungService;

    private final ELValueCacheGroup cdWarenkorbCacheGroup;

    private final ELValueCache<CdWarenkorb> cdWarenkorbValueCache;

    private final ELValueCacheGroup downloadWarenkorbCacheGroup;

    private final ELValueCache<DownloadWarenkorb> downloadWarenkorbValueCache;

    private final ELValueCache<Boolean> maxDownloadsProTagErreicht;

    private final ELValueCache<Boolean> maxDownloadsProMonatErreicht;

    @Autowired
    MeinWarenkorb(final Hoerernummer hoerernummer,
                  final HoerbuchkatalogService hoerbuchkatalogService,
                  final BestellungService bestellungService) {
        LOGGER.trace("Initializing");
        this.hoerernummer = hoerernummer;
        this.bestellungService = bestellungService;
        // CD Warenkorb
        cdWarenkorbValueCache = new ELValueCache<>(null,
                () -> bestellungService.cdWarenkorbKopie(hoerernummer));
        cdWarenkorbCacheGroup = new ELValueCacheGroup(cdWarenkorbValueCache);
        // Download Warenkorb
        downloadWarenkorbValueCache = new ELValueCache<>(null,
                () -> bestellungService.downloadWarenkorbKopie(hoerernummer));
        maxDownloadsProTagErreicht = new ELValueCache<>(Boolean.FALSE,
                () -> bestellungService.isMaxDownloadsProTagErreicht(hoerernummer));
        maxDownloadsProMonatErreicht = new ELValueCache<>(Boolean.FALSE,
                () -> bestellungService.isMaxDownloadsProMonatErreicht(hoerernummer));
        downloadWarenkorbCacheGroup = new ELValueCacheGroup(downloadWarenkorbValueCache,
                maxDownloadsProTagErreicht, maxDownloadsProMonatErreicht);
        // Hörbuch
        hoerbuchValueCache = new ELFunctionCache<>(
                titelnummer -> hoerbuchkatalogService.hole(hoerernummer, titelnummer));
    }

    public void bestellungAufgegeben() {
        cdWarenkorbCacheGroup.invalidateAll();
        downloadWarenkorbCacheGroup.invalidateAll();
        hoerbuchValueCache.invalidateAll();
    }

    public int getAnzahl() {
        LOGGER.trace("");
        return cdWarenkorbValueCache.get().getAnzahl() +
                downloadWarenkorbValueCache.get().getAnzahl();
    }

    //
    // CD Warenkorb
    //

    public CdWarenkorb getCds() {
        LOGGER.trace("");
        return cdWarenkorbValueCache.get();
    }

    public boolean cdEnthalten(final Titelnummer titelnummer) {
        LOGGER.trace("");
        return cdWarenkorbValueCache.get().enthalten(titelnummer);
    }

    public void cdHinzufuegen(final Titelnummer titelnummer) {
        LOGGER.trace("");
        cdWarenkorbCacheGroup.invalidateAll();
        bestellungService.inDenCdWarenkorb(hoerernummer, titelnummer);
    }

    public void cdEntfernen(final Titelnummer titelnummer) {
        LOGGER.trace("");
        cdWarenkorbCacheGroup.invalidateAll();
        bestellungService.ausDemCdWarenkorbEntfernen(hoerernummer, titelnummer);
    }

    //
    // Download Warenkorb
    //

    public DownloadWarenkorb getDownloads() {
        LOGGER.trace("");
        return downloadWarenkorbValueCache.get();
    }

    public boolean downloadEnthalten(final Titelnummer titelnummer) {
        LOGGER.trace("");
        return downloadWarenkorbValueCache.get().enthalten(titelnummer);
    }

    public void downloadHinzufuegen(final Titelnummer titelnummer) {
        LOGGER.trace("");
        downloadWarenkorbCacheGroup.invalidateAll();
        bestellungService.inDenDownloadWarenkorb(hoerernummer, titelnummer);
    }

    public void downloadEntfernen(final Titelnummer titelnummer) {
        LOGGER.trace("");
        downloadWarenkorbCacheGroup.invalidateAll();
        bestellungService.ausDemDownloadWarenkorbEntfernen(hoerernummer, titelnummer);
    }

    public boolean isMaxDownloadsProTagErreicht() {
        LOGGER.trace("");
        return maxDownloadsProTagErreicht.get();
    }

    public boolean isMaxDownloadsProMonatErreicht() {
        LOGGER.trace("");
        return maxDownloadsProMonatErreicht.get();
    }

    //
    // Hörbuch
    //

    public boolean downloadFuerHoererVerfuegbar(final Titelnummer titelnummer) {
        LOGGER.trace("{}", titelnummer);
        final boolean b = !isMaxDownloadsProTagErreicht()
                && !isMaxDownloadsProMonatErreicht()
                && hoerbuchValueCache.get(titelnummer).isDownloadbar();
        LOGGER.debug("{} = {}", titelnummer, b);
        return b;
    }

    public String hoerbuchAutor(final Titelnummer titelnummer) {
        LOGGER.trace("");
        return hoerbuchValueCache.get(titelnummer).getAutor();
    }

    public String hoerbuchTitel(final Titelnummer titelnummer) {
        LOGGER.trace("");
        return hoerbuchValueCache.get(titelnummer).getTitel();
    }

}

/*
 * Copyright (C) 2011-2018 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchkatalog.ui.katalog;

import wbh.bookworm.hoerbuchkatalog.app.bestellung.BestellungService;
import wbh.bookworm.hoerbuchkatalog.app.katalog.HoerbuchkatalogService;
import wbh.bookworm.hoerbuchkatalog.domain.bestellung.CdWarenkorb;
import wbh.bookworm.hoerbuchkatalog.domain.bestellung.DownloadWarenkorb;
import wbh.bookworm.hoerbuchkatalog.domain.katalog.Hoerbuch;
import wbh.bookworm.hoerbuchkatalog.domain.katalog.Titelnummer;

import aoc.jsf.ELFunctionCache;
import aoc.jsf.ELValueCache;
import aoc.jsf.ELValueCacheGroup;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.Serializable;

@Component
@org.springframework.web.context.annotation.SessionScope
//@org.springframework.context.annotation.Scope("session")
//@javax.enterprise.context.SessionScoped
public class MeinWarenkorb implements Serializable {

    private static final transient Logger LOGGER = LoggerFactory.getLogger(MeinWarenkorb.class);

    //
    // Hörer
    //

    private final HoererSession hoererSession;

    //
    // Hörbuchkatalog
    //

    private final transient ELFunctionCache<Titelnummer, Hoerbuch> hoerbuchValueCache;

    //
    // Bestellung
    //

    private final transient BestellungService bestellungService;

    private final transient ELValueCacheGroup cdWarenkorbCacheGroup;

    private final transient ELValueCache<CdWarenkorb> cdWarenkorbValueCache;

    private final transient ELValueCacheGroup downloadWarenkorbCacheGroup;

    private final transient ELValueCache<DownloadWarenkorb> downloadWarenkorbValueCache;

    private final transient ELValueCache<Boolean> maxDownloadsProTagErreicht;

    private final transient ELValueCache<Boolean> maxDownloadsProMonatErreicht;

    @Autowired
    MeinWarenkorb(final HoererSession hoererSession,
                  final HoerbuchkatalogService hoerbuchkatalogService,
                  final BestellungService bestellungService) {
        LOGGER.trace("Initialisiere für Hörer {}", hoererSession.getHoerernummer());
        this.hoererSession = hoererSession;
        this.bestellungService = bestellungService;
        // CD Warenkorb
        cdWarenkorbValueCache = new ELValueCache<>(null,
                () -> bestellungService.cdWarenkorbKopie(hoererSession.getBestellungSessionId()));
        cdWarenkorbCacheGroup = new ELValueCacheGroup(cdWarenkorbValueCache);
        // Download Warenkorb
        downloadWarenkorbValueCache = new ELValueCache<>(null,
                () -> bestellungService.downloadWarenkorbKopie(hoererSession.getBestellungSessionId()));
        maxDownloadsProTagErreicht = new ELValueCache<>(Boolean.FALSE,
                () -> bestellungService.isMaxDownloadsProTagErreicht(hoererSession.getBestellungSessionId()));
        maxDownloadsProMonatErreicht = new ELValueCache<>(Boolean.FALSE,
                () -> bestellungService.isMaxDownloadsProMonatErreicht(hoererSession.getBestellungSessionId()));
        downloadWarenkorbCacheGroup = new ELValueCacheGroup(downloadWarenkorbValueCache,
                maxDownloadsProTagErreicht, maxDownloadsProMonatErreicht);
        // Hörbuch
        hoerbuchValueCache = new ELFunctionCache<>(
                titelnummer -> hoerbuchkatalogService.hole(hoererSession.getHoerernummer(), titelnummer));
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
        bestellungService.inDenCdWarenkorb(
                hoererSession.getBestellungSessionId(), titelnummer);
    }

    public void cdEntfernen(final Titelnummer titelnummer) {
        LOGGER.trace("");
        cdWarenkorbCacheGroup.invalidateAll();
        bestellungService.ausDemCdWarenkorbEntfernen(
                hoererSession.getBestellungSessionId(), titelnummer);
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
        bestellungService.inDenDownloadWarenkorb(
                hoererSession.getBestellungSessionId(), titelnummer);
    }

    public void downloadEntfernen(final Titelnummer titelnummer) {
        LOGGER.trace("");
        downloadWarenkorbCacheGroup.invalidateAll();
        bestellungService.ausDemDownloadWarenkorbEntfernen(
                hoererSession.getBestellungSessionId(), titelnummer);
    }

    public boolean isMaxDownloadsProTagErreicht() {
        LOGGER.trace("");
        return hoererSession.getHoerernummer().isBekannt()
                && maxDownloadsProTagErreicht.get();
    }

    public boolean isMaxDownloadsProMonatErreicht() {
        LOGGER.trace("");
        return hoererSession.getHoerernummer().isBekannt()
                && maxDownloadsProMonatErreicht.get();
    }

    public boolean isDownloadHinzufuegenAnzeigen(final Titelnummer titelnummer) {
        return hoererSession.isHoererIstBekannt()
                && hoerbuchValueCache.get(titelnummer).isDownloadbar()
                && downloadFuerHoererVerfuegbar(titelnummer)
                && !downloadEnthalten(titelnummer);
    }

    public boolean isDownloadEntfernenAnzeigen(final Titelnummer titelnummer) {
        return hoererSession.isHoererIstBekannt()
                // TODO && hoerbuchValueCache.get(titelnummer).isDownloadbar()
                && downloadEnthalten(titelnummer);
    }

    //
    // Bestellung
    //

    void bestellungAufgegeben() {
        cdWarenkorbCacheGroup.invalidateAll();
        downloadWarenkorbCacheGroup.invalidateAll();
        hoerbuchValueCache.invalidateAll();
    }

    public int getAnzahl() {
        LOGGER.trace("");
        return cdWarenkorbValueCache.get().getAnzahl() +
                downloadWarenkorbValueCache.get().getAnzahl();
    }

}

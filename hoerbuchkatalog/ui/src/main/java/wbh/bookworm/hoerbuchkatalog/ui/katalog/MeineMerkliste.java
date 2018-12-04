/*
 * Copyright (C) 2011-2018 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchkatalog.ui.katalog;

import wbh.bookworm.hoerbuchkatalog.app.bestellung.MerklisteService;
import wbh.bookworm.hoerbuchkatalog.app.katalog.HoerbuchkatalogService;
import wbh.bookworm.hoerbuchkatalog.domain.bestellung.Merkliste;
import wbh.bookworm.hoerbuchkatalog.domain.hoerer.Hoerernummer;
import wbh.bookworm.hoerbuchkatalog.domain.katalog.Hoerbuch;
import wbh.bookworm.hoerbuchkatalog.domain.katalog.Titelnummer;

import aoc.jsf.ELFunctionCache;
import aoc.jsf.ELValueCache;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.SessionScope;

import java.util.Set;

@Component
@SessionScope
public class MeineMerkliste {

    private static final Logger LOGGER = LoggerFactory.getLogger(MeineMerkliste.class);

    //
    // Hörer
    //

    private final Hoerernummer hoerernummer;

    //
    // Merkliste
    //

    private final MerklisteService merklisteService;

    private final ELValueCache<Merkliste> merklisteValueCache;

    //
    // Bestellung
    //

    private final MeinWarenkorb meinWarenkorb;

    //
    // Hörbuchkatalog
    //

    private final ELFunctionCache<Titelnummer, Hoerbuch> hoerbuchValueCache;

    @Autowired
    MeineMerkliste(final HoererSession hoererSession,
                   final MerklisteService merklisteService,
                   final MeinWarenkorb meinWarenkorb,
                   final HoerbuchkatalogService hoerbuchkatalogService) {
        final Hoerernummer hoerernummer = hoererSession.hoerernummer();
        LOGGER.trace("Initialisiere für Hörer {}", hoerernummer);
        this.hoerernummer = hoerernummer;
        this.meinWarenkorb = meinWarenkorb;
        this.merklisteService = merklisteService;
        // Merkliste
        merklisteValueCache = new ELValueCache<>(null,
                () -> merklisteService.merklisteKopie(hoerernummer));
        // Hörbuch
        hoerbuchValueCache = new ELFunctionCache<>(
                titelnummer -> hoerbuchkatalogService.hole(hoerernummer, titelnummer));
    }

    public int getAnzahl() {
        LOGGER.trace("");
        return merklisteValueCache.get().getAnzahl();
    }

    public Set<Titelnummer> getTitelnummern() {
        LOGGER.trace("");
        return merklisteValueCache.get().getTitelnummern();
    }

    public boolean enthalten(final Titelnummer titelnummer) {
        LOGGER.trace("");
        return merklisteValueCache.get().enthalten(titelnummer);
    }

    public void hinzufuegen(final Titelnummer titelnummer) {
        LOGGER.trace("");
        merklisteValueCache.invalidate();
        merklisteService.hinzufuegen(hoerernummer, titelnummer);
    }

    public void entfernen(final Titelnummer titelnummer) {
        LOGGER.trace("");
        merklisteValueCache.invalidate();
        merklisteService.entfernen(hoerernummer, titelnummer);
    }

    public void inCdWarenkorbVerschieben(final Titelnummer titelnummer) {
        LOGGER.trace("");
        merklisteValueCache.invalidate();
        meinWarenkorb.cdHinzufuegen(titelnummer);
        merklisteService.entfernen(hoerernummer, titelnummer);
    }

    public void inDownloadWarenkorbVerschieben(final Titelnummer titelnummer) {
        LOGGER.trace("");
        merklisteValueCache.invalidate();
        meinWarenkorb.downloadHinzufuegen(titelnummer);
        merklisteService.entfernen(hoerernummer, titelnummer);
    }

    //
    // Hörbuch
    //

    public boolean downloadFuerHoererVerfuegbar(final Titelnummer titelnummer) {
        LOGGER.trace("{}", titelnummer);
        final boolean b = !meinWarenkorb.isMaxDownloadsProTagErreicht()
                && !meinWarenkorb.isMaxDownloadsProMonatErreicht()
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

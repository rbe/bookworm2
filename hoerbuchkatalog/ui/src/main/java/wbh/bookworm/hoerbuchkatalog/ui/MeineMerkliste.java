/*
 * Copyright (C) 2011-2018 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchkatalog.ui;

import wbh.bookworm.hoerbuchkatalog.app.bestellung.MerklisteService;
import wbh.bookworm.hoerbuchkatalog.app.katalog.HoerbuchkatalogService;
import wbh.bookworm.hoerbuchkatalog.domain.hoerer.Hoerernummer;
import wbh.bookworm.hoerbuchkatalog.domain.katalog.Titelnummer;
import wbh.bookworm.platform.jsf.ELValueCache;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.SessionScope;

import java.util.Set;

@Component
@SessionScope
class MeineMerkliste {

    private static final Logger LOGGER = LoggerFactory.getLogger(MeineMerkliste.class);

    private final Hoerernummer hoerernummer;

    private final MerklisteService merklisteService;

    private final MeinWarenkorb meinWarenkorb;

    private final HoerbuchkatalogService hoerbuchkatalogService;

    private final ELValueCache<Integer> anzahlValueCache;

    @Autowired
    MeineMerkliste(final Hoerernummer hoerernummer,
                   final MerklisteService merklisteService,
                   final MeinWarenkorb meinWarenkorb,
                   final HoerbuchkatalogService hoerbuchkatalogService) {
        LOGGER.trace("Initializing");
        this.hoerernummer = hoerernummer;
        this.merklisteService = merklisteService;
        this.meinWarenkorb = meinWarenkorb;
        this.hoerbuchkatalogService = hoerbuchkatalogService;
        this.anzahlValueCache = new ELValueCache<>(0,
                () -> merklisteService.anzahl(hoerernummer));
    }

    public int getAnzahl() {
        LOGGER.trace("");
        return anzahlValueCache.get();
    }

    public Set<Titelnummer> getTitelnummern() {
        LOGGER.trace("");
        return merklisteService.titelnummernAufMerkliste(hoerernummer);
    }

    public boolean enthalten(final Titelnummer titelnummer) {
        LOGGER.trace("");
        return merklisteService.enthalten(hoerernummer, titelnummer);
    }

    public void hinzufuegen(final Titelnummer titelnummer) {
        LOGGER.trace("");
        anzahlValueCache.invalidate();
        merklisteService.hinzufuegen(hoerernummer, titelnummer);
    }

    public void entfernen(final Titelnummer titelnummer) {
        LOGGER.trace("");
        anzahlValueCache.invalidate();
        merklisteService.entfernen(hoerernummer, titelnummer);
    }

    public void inCdWarenkorbVerschieben(final Titelnummer titelnummer) {
        LOGGER.trace("");
        anzahlValueCache.invalidate();
        meinWarenkorb.cdHinzufuegen(titelnummer);
        merklisteService.entfernen(hoerernummer, titelnummer);
    }

    public void inDownloadWarenkorbVerschieben(final Titelnummer titelnummer) {
        LOGGER.trace("");
        anzahlValueCache.invalidate();
        meinWarenkorb.downloadHinzufuegen(titelnummer);
        merklisteService.entfernen(hoerernummer, titelnummer);
    }

    public String hoerbuchAutor(final Titelnummer titelnummer) {
        LOGGER.trace("");
        return hoerbuchkatalogService.hole(hoerernummer, titelnummer).getAutor();
    }

    public String hoerbuchTitel(final Titelnummer titelnummer) {
        LOGGER.trace("");
        return hoerbuchkatalogService.hole(hoerernummer, titelnummer).getTitel();
    }

    public boolean hoerbuchDownloadbar(final Titelnummer titelnummer) {
        LOGGER.trace("");
        return hoerbuchkatalogService.hole(hoerernummer, titelnummer).isDownloadbar();
    }

}

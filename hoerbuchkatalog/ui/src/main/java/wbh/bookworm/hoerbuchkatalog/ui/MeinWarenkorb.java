/*
 * Copyright (C) 2011-2018 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchkatalog.ui;

import wbh.bookworm.hoerbuchkatalog.app.katalog.HoerbuchkatalogService;
import wbh.bookworm.hoerbuchkatalog.domain.bestellung.CdWarenkorb;
import wbh.bookworm.hoerbuchkatalog.domain.bestellung.DownloadWarenkorb;
import wbh.bookworm.hoerbuchkatalog.domain.hoerer.Hoerernummer;
import wbh.bookworm.hoerbuchkatalog.domain.katalog.Titelnummer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.SessionScope;

@Component
@SessionScope
class MeinWarenkorb {

    private final Hoerernummer hoerernummer;

    private final HoerbuchkatalogService hoerbuchkatalogService;

    private final CdWarenkorb cdWarenkorb;

    private final DownloadWarenkorb downloadWarenkorb;

    @Autowired
    MeinWarenkorb(final Hoerernummer hoerernummer, final HoerbuchkatalogService hoerbuchkatalogService) {
        this.hoerernummer = hoerernummer;
        this.hoerbuchkatalogService = hoerbuchkatalogService;
        this.cdWarenkorb = new CdWarenkorb(hoerernummer);
        this.downloadWarenkorb = new DownloadWarenkorb(hoerernummer);
    }

    public int getTotaleAnzahl() {
        return cdWarenkorb.getAnzahl() + downloadWarenkorb.getAnzahl();
    }

    public CdWarenkorb getCd() {
        return cdWarenkorb;
    }

    public boolean cdEnthalten(final Titelnummer titelnummer) {
        return cdWarenkorb.enthalten(titelnummer);
    }

    public void cdHinzufuegen(final Titelnummer titelnummer) {
        hoerbuchkatalogService.finde(hoerernummer, titelnummer).alsCdInDenWarenkorbLegen(hoerernummer);
        cdWarenkorb.hinzufuegen(titelnummer);
    }

    public void cdEntfernen(final Titelnummer titelnummer) {
        cdWarenkorb.entfernen(titelnummer);
    }

    public DownloadWarenkorb getDownload() {
        return downloadWarenkorb;
    }

    public boolean downloadEnthalten(final Titelnummer titelnummer) {
        return downloadWarenkorb.enthalten(titelnummer);
    }

    public void downloadHinzufuegen(final Titelnummer titelnummer) {
        downloadWarenkorb.hinzufuegen(titelnummer);
    }

    public void downloadEntfernen(final Titelnummer titelnummer) {
        downloadWarenkorb.entfernen(titelnummer);
    }

    public boolean isMaxDownloadsProTagErreicht() {
        return downloadWarenkorb.isMaxDownloadsProTagErreicht();
    }

    public boolean isMaxDownloadsProMonatErreicht() {
        return downloadWarenkorb.isMaxDownloadsProMonatErreicht();
    }

}

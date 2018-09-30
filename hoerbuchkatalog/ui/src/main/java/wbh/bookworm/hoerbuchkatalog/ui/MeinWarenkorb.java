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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.SessionScope;

@Component
@SessionScope
class MeinWarenkorb {

    private final Hoerernummer hoerernummer;

    private final HoerbuchkatalogService hoerbuchkatalogService;

    private final BestellungService bestellungService;

    @Autowired
    MeinWarenkorb(final Hoerernummer hoerernummer,
                  final HoerbuchkatalogService hoerbuchkatalogService,
                  final BestellungService bestellungService) {
        this.hoerernummer = hoerernummer;
        this.hoerbuchkatalogService = hoerbuchkatalogService;
        this.bestellungService = bestellungService;
    }

    public int getTotaleAnzahl() {
        return bestellungService.anzahlHoerbuecher(hoerernummer);
    }

    public CdWarenkorb getCd() {
        return bestellungService.cdWarenkorbKopie(hoerernummer);
    }

    public boolean cdEnthalten(final Titelnummer titelnummer) {
        return bestellungService.imCdWarenkorbEnthalten(hoerernummer, titelnummer);
    }

    public void cdHinzufuegen(final Titelnummer titelnummer) {
        bestellungService.inDenCdWarenkorb(hoerernummer, titelnummer);
    }

    public void cdEntfernen(final Titelnummer titelnummer) {
        bestellungService.ausDemCdWarenkorbEntfernen(hoerernummer, titelnummer);
    }

    public DownloadWarenkorb getDownload() {
        return bestellungService.downloadWarenkorbKopie(hoerernummer);
    }

    public boolean downloadVerfuegbar(final Titelnummer titelnummer) {
        return hoerbuchkatalogService.hoerbuchDownloadbar(hoerernummer, titelnummer);
    }

    public boolean downloadEnthalten(final Titelnummer titelnummer) {
        return bestellungService.imDownloadWarenkorbEnthalten(hoerernummer, titelnummer);
    }

    public void downloadHinzufuegen(final Titelnummer titelnummer) {
        bestellungService.inDenDownloadWarenkorb(hoerernummer, titelnummer);
    }

    public void downloadEntfernen(final Titelnummer titelnummer) {
        bestellungService.ausDemDownloadWarenkorbEntfernen(hoerernummer, titelnummer);
    }

    public boolean isMaxDownloadsProTagErreicht() {
        return bestellungService.isMaxDownloadsProTagErreicht();
    }

    public boolean isMaxDownloadsProMonatErreicht() {
        return bestellungService.isMaxDownloadsProMonatErreicht();
    }

    public String hoerbuchAutor(final Titelnummer titelnummer) {
        return hoerbuchkatalogService.hole(hoerernummer, titelnummer).getAutor();
    }

    public String hoerbuchTitel(final Titelnummer titelnummer) {
        return hoerbuchkatalogService.hole(hoerernummer, titelnummer).getTitel();
    }

}

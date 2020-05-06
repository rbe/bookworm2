/*
 * Copyright (C) 2011-2018 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchkatalog.ui.katalog;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;

import wbh.bookworm.hoerbuchkatalog.app.bestellung.WarenkorbService;
import wbh.bookworm.hoerbuchkatalog.domain.bestellung.CdWarenkorb;
import wbh.bookworm.hoerbuchkatalog.domain.bestellung.DownloadWarenkorb;
import wbh.bookworm.shared.domain.hoerbuch.Titelnummer;

@Component
@RequestScope
public class MeinWarenkorb {

    private static final Logger LOGGER = LoggerFactory.getLogger(MeinWarenkorb.class);

    private final HoererSession hoererSession;

    private final WarenkorbService warenkorbService;

    @Autowired
    MeinWarenkorb(final HoererSession hoererSession,
                  final WarenkorbService warenkorbService) {
        LOGGER.trace("Initialisiere für {}", hoererSession);
        this.hoererSession = hoererSession;
        this.warenkorbService = warenkorbService;
    }

    //
    // Hörbuchkatalog
    //

    // TODO Duplikat mit MeineMerkliste#downloadFuerHoererVerfuegbar
    private boolean downloadFuerHoererVerfuegbar(final Titelnummer titelnummer) {
        LOGGER.trace("{}", titelnummer);
        final boolean b = !isMaxDownloadsProTagErreicht()
                && !isMaxDownloadsProMonatErreicht()
                && hoererSession.hoerbuchIstDownloadbar(titelnummer);
        LOGGER.debug("Download für Hörbuch {} {} vergfübar", titelnummer, b ? "ist" : "ist nicht");
        return b;
    }

    public String hoerbuchAutor(final Titelnummer titelnummer) {
        LOGGER.trace("");
        return hoererSession.autorCache(titelnummer);
    }

    public String hoerbuchTitel(final Titelnummer titelnummer) {
        LOGGER.trace("");
        return hoererSession.titelCache(titelnummer);
    }

    //
    // CD Warenkorb
    //

    public CdWarenkorb getCdWarenkorb() {
        LOGGER.trace("");
        return hoererSession.cdWarenkorb();
    }

    public boolean cdEnthalten(final Titelnummer titelnummer) {
        LOGGER.trace("");
        return hoererSession.cdEnthalten(titelnummer);
    }

    public void cdHinzufuegen(final Titelnummer titelnummer) {
        LOGGER.trace("");
        warenkorbService.inDenCdWarenkorb(
                hoererSession.getBestellungSessionId(), hoererSession.getHoerernummer(), titelnummer);
        /* TODO CdInDenWarenkorbGelegtEvent */hoererSession.cdWarenkorbVergessen();
    }

    public void cdEntfernen(final Titelnummer titelnummer) {
        LOGGER.trace("");
        warenkorbService.ausDemCdWarenkorbEntfernen(
                hoererSession.getBestellungSessionId(), hoererSession.getHoerernummer(), titelnummer);
        /* TODO CdInDenWarenkorbGelegtEvent */hoererSession.cdWarenkorbVergessen();
    }

    //
    // Download Warenkorb
    //

    public DownloadWarenkorb getDownloadWarenkorb() {
        LOGGER.trace("");
        return hoererSession.downloadWarenkorb();
    }

    public boolean downloadEnthalten(final Titelnummer titelnummer) {
        LOGGER.trace("");
        return hoererSession.downloadEnthalten(titelnummer);
    }

    public void downloadHinzufuegen(final Titelnummer titelnummer) {
        LOGGER.trace("");
        warenkorbService.inDenDownloadWarenkorb(
                hoererSession.getBestellungSessionId(), hoererSession.getHoerernummer(), titelnummer);
        /* TODO DownloadInDenWarenkorbGelegtEvent */hoererSession.downloadWarenkorbVergessen();
    }

    public void downloadEntfernen(final Titelnummer titelnummer) {
        LOGGER.trace("");
        warenkorbService.ausDemDownloadWarenkorbEntfernen(
                hoererSession.getBestellungSessionId(), hoererSession.getHoerernummer(), titelnummer);
        /* TODO DownloadInDenWarenkorbGelegtEvent */hoererSession.downloadWarenkorbVergessen();
    }

    // TODO Gehört zu MeineBestellung
    public boolean isMaxDownloadsProTagErreicht() {
        LOGGER.trace("");
        return hoererSession.maxDownloadsProTagErreicht();
    }

    // TODO Gehört zu MeineBestellung
    public boolean isMaxDownloadsProMonatErreicht() {
        LOGGER.trace("");
        return hoererSession.maxDownloadsProMonatErreicht();
    }

    public boolean isDownloadHinzufuegenAnzeigen(final Titelnummer titelnummer) {
        return hoererSession.isHoererIstBekannt()
                // TODO hoerbuchIstDownloadbar und downloadFuerHoererVerfuegbar kombinieren, Hoerernummer+Titelnummer
                && hoererSession.hoerbuchIstDownloadbar(titelnummer)
                && downloadFuerHoererVerfuegbar(titelnummer)
                && !downloadEnthalten(titelnummer);
    }

    public boolean isDownloadEntfernenAnzeigen(final Titelnummer titelnummer) {
        return hoererSession.isHoererIstBekannt()
                && downloadEnthalten(titelnummer);
    }

    //
    // Bestellung
    //

    public int getAnzahlGesamt() {
        LOGGER.trace("");
        return hoererSession.anzahlImWarenkorbGesamt();
    }

    /* TODO BestellungAufgegebenEvent
    void bestellungAufgegeben() {
        hoererSession.cdWarenkorbVergessen();
        hoererSession.downloadWarenkorbVergessen();
    }
    */

}

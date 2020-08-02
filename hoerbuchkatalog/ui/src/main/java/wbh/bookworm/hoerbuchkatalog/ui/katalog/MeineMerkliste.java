/*
 * Copyright (C) 2011-2018 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchkatalog.ui.katalog;

import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;

import wbh.bookworm.hoerbuchkatalog.app.bestellung.MerklisteService;
import wbh.bookworm.shared.domain.hoerbuch.Titelnummer;
import wbh.bookworm.shared.domain.mandant.Hoerernummer;

@Component
@RequestScope
public class MeineMerkliste {

    private static final Logger LOGGER = LoggerFactory.getLogger(MeineMerkliste.class);

    //
    // Hörer
    //

    private final HoererSession hoererSession;

    private final Hoerernummer hoerernummer;

    //
    // Merkliste
    //

    // TODO Proxy für Service, welcher das Caching handhabt, anstelle über HoererSession
    private final MerklisteService merklisteService;

    //
    // Bestellung
    //

    private final MeinWarenkorb meinWarenkorb;

    @Autowired
    MeineMerkliste(final HoererSession hoererSession,
                   final MerklisteService merklisteService,
                   final MeinWarenkorb meinWarenkorb) {
        LOGGER.trace("Initialisiere für {}", hoererSession);
        this.hoererSession = hoererSession;
        this.hoerernummer = hoererSession.getHoerernummer();
        this.meinWarenkorb = meinWarenkorb;
        this.merklisteService = merklisteService;
    }

    //
    // Hörbuchkatalog
    //

    // TODO Duplikat von MeinWarenkorb#downloadFuerHoererVerfuegbar
    public boolean downloadFuerHoererVerfuegbar(final Titelnummer titelnummer) {
        LOGGER.trace("{}", titelnummer);
        final boolean b = !meinWarenkorb.isMaxDownloadsProTagErreicht()
                && !meinWarenkorb.isMaxDownloadsProMonatErreicht()
                && hoererSession.hoerbuchIstDownloadbar(titelnummer);
        LOGGER.debug("Download für Hörbuch {} {} vergfübar", titelnummer, b ? "ist" : "ist nicht");
        return b;
    }

    public String hoerbuchAutor(final Titelnummer titelnummer) {
        LOGGER.trace("");
        return hoererSession.hoerbuchValueCache().get(titelnummer).getAutor();
    }

    public String hoerbuchTitel(final Titelnummer titelnummer) {
        LOGGER.trace("");
        return hoererSession.hoerbuchValueCache().get(titelnummer).getTitel();
    }

    //
    // Merkliste
    //

    public int getAnzahl() {
        LOGGER.trace("");
        return hoererSession.anzahlTitelnummernAufMerkliste();
    }

    public Set<Titelnummer> getTitelnummern() {
        LOGGER.trace("");
        return hoererSession.titelnummernAufMerkliste();
    }

    public boolean enthalten(final Titelnummer titelnummer) {
        LOGGER.trace("");
        return hoererSession.titelnummernAufMerklisteEnthalten(titelnummer);
    }

    public void hinzufuegen(final Titelnummer titelnummer) {
        LOGGER.trace("");
        hoererSession.merklisteVergessen();
        merklisteService.hinzufuegen(hoerernummer, titelnummer);
    }

    public void entfernen(final Titelnummer titelnummer) {
        LOGGER.trace("");
        hoererSession.merklisteVergessen();
        merklisteService.entfernen(hoerernummer, titelnummer);
    }

    public boolean isMerklisteHinzufuegenAnzeigen(final Titelnummer titelnummer) {
        return hoererSession.isHoererIstBekannt() && !enthalten(titelnummer);
    }

    public boolean isMerklisteEntfernenAnzeigen(final Titelnummer titelnummer) {
        return hoererSession.isHoererIstBekannt() && enthalten(titelnummer);
    }

    public void inCdWarenkorbVerschieben(final Titelnummer titelnummer) {
        LOGGER.trace("");
        hoererSession.merklisteVergessen();
        meinWarenkorb.cdHinzufuegen(titelnummer);
        merklisteService.entfernen(hoerernummer, titelnummer);
    }

    public void inDownloadWarenkorbVerschieben(final Titelnummer titelnummer) {
        LOGGER.trace("");
        hoererSession.merklisteVergessen();
        meinWarenkorb.downloadHinzufuegen(titelnummer);
        merklisteService.entfernen(hoerernummer, titelnummer);
    }

}

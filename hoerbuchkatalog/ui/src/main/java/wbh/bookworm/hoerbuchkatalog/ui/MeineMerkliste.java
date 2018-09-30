/*
 * Copyright (C) 2011-2018 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchkatalog.ui;

import wbh.bookworm.hoerbuchkatalog.app.bestellung.BestellungService;
import wbh.bookworm.hoerbuchkatalog.app.bestellung.MerklisteService;
import wbh.bookworm.hoerbuchkatalog.domain.hoerer.Hoerernummer;
import wbh.bookworm.hoerbuchkatalog.domain.katalog.Titelnummer;

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

    private final BestellungService bestellungService;

    @Autowired
    MeineMerkliste(final Hoerernummer hoerernummer,
                   final MerklisteService merklisteService, final BestellungService bestellungService) {
        this.hoerernummer = hoerernummer;
        this.merklisteService = merklisteService;
        this.bestellungService = bestellungService;
    }

    public int getAnzahl() {
        return merklisteService.anzahl(hoerernummer);
    }

    public Set<Titelnummer> getTitelnummern() {
        return merklisteService.titelnummernAufMerkliste(hoerernummer);
    }

    public boolean enthalten(final Titelnummer titelnummer) {
        return merklisteService.enthalten(hoerernummer, titelnummer);
    }

    public void hinzufuegen(final Titelnummer titelnummer) {
        merklisteService.hinzufuegen(hoerernummer, titelnummer);
    }

    public void entfernen(final Titelnummer titelnummer) {
        merklisteService.entfernen(hoerernummer, titelnummer);
    }

    public void inCdWarenkorbVerschieben(final Titelnummer titelnummer) {
        bestellungService.inDenCdWarenkorb(hoerernummer, titelnummer);
        merklisteService.entfernen(hoerernummer, titelnummer);
    }

    public void inDownloadWarenkorbVerschieben(final Titelnummer titelnummer) {
        bestellungService.inDenDownloadWarenkorb(hoerernummer, titelnummer);
        merklisteService.entfernen(hoerernummer, titelnummer);
    }

}

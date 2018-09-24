/*
 * Copyright (C) 2011-2018 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchkatalog.ui;

import wbh.bookworm.hoerbuchkatalog.app.bestellung.BestellungService;
import wbh.bookworm.hoerbuchkatalog.domain.bestellung.Merkliste;
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

    private final BestellungService bestellungService;

    @Autowired
    MeineMerkliste(final Hoerernummer hoerernummer,
                   final BestellungService bestellungService) {
        this.hoerernummer = hoerernummer;
        this.bestellungService = bestellungService;
    }

    public int getAnzahl() {
        return bestellungService.anzahlAufMerkliste(hoerernummer);
    }

    public Set<Titelnummer> getTitelnummern() {
            return bestellungService.titelnummernAufMerkliste(hoerernummer);
    }

    public boolean enthalten(final Titelnummer titelnummer) {
        return bestellungService.titelnummerAufMerklisteEnthalten(titelnummer);
    }

    public void hinzufuegen(final Titelnummer titelnummer) {
        merkliste.hinzufuegen(titelnummer);
    }

    public void entfernen(final Titelnummer titelnummer) {
        merkliste.entfernen(titelnummer);
    }

    public void inCdWarenkorbVerschieben(final Titelnummer titelnummer) {
        LOGGER.warn("NOT IMPLEMENTED YET");
    }

    public void inDownloadWarenkorbVerschieben(final Titelnummer titelnummer) {
        LOGGER.warn("NOT IMPLEMENTED YET");
    }

}

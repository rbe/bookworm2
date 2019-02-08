/*
 * Copyright (C) 2011-2019 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchkatalog.ui.nutzerdaten;

import wbh.bookworm.hoerbuchkatalog.app.hoerer.HoererService;
import wbh.bookworm.hoerbuchkatalog.app.lieferung.CdLieferungService;
import wbh.bookworm.hoerbuchkatalog.domain.hoerer.Hoerer;
import wbh.bookworm.hoerbuchkatalog.domain.hoerer.HoererEmail;
import wbh.bookworm.hoerbuchkatalog.domain.hoerer.Hoerername;
import wbh.bookworm.hoerbuchkatalog.domain.hoerer.Hoerernummer;
import wbh.bookworm.hoerbuchkatalog.domain.hoerer.Nachname;
import wbh.bookworm.hoerbuchkatalog.domain.hoerer.Vorname;
import wbh.bookworm.hoerbuchkatalog.ui.katalog.HoererSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;

import java.time.format.DateTimeFormatter;

@Component
@RequestScope
public class MeineDaten {

    private static final Logger LOGGER = LoggerFactory.getLogger(MeineDaten.class);

    private final Hoerernummer hoerernummer;

    private final HoererService hoererService;

    private final CdLieferungService cdLieferungService;

    @Autowired
    public MeineDaten(final HoererSession hoererSession,
                      final HoererService hoererService,
                      final CdLieferungService cdLieferungService) {
        this.hoerernummer = hoererSession.getHoerernummer();
        this.hoererService = hoererService;
        this.cdLieferungService = cdLieferungService;
    }

    public Hoerernummer getHoerernummer() {
        return hoerernummer;
    }

    public Hoerername getHoerername() {
        return hoererService.hoerer(hoerernummer)
                .map(Hoerer::getHoerername)
                .orElse(Hoerername.UNBEKANNT);
    }

    public Nachname getNachname() {
        return hoererService.hoerer(hoerernummer)
                .map(Hoerer::getNachname)
                .orElse(Nachname.UNBEKANNT);
    }

    public String getNachnamenszusatz() {
        return hoererService.hoerer(hoerernummer)
                .map(Hoerer::getName2)
                .orElse("unbekannt");
    }

    public Vorname getVorname() {
        return hoererService.hoerer(hoerernummer)
                .map(Hoerer::getVorname)
                .orElse(Vorname.UNBEKANNT);
    }

    public String getGeburtsdatumAufDeutsch() {
        return hoererService.hoerer(hoerernummer)
                .map(h -> h.getGeburtsdatum().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")))
                .orElse("unbekannt");
    }

    public String getAdresszusatz() {
        return hoererService.hoerer(hoerernummer)
                .map(Hoerer::getAdresszusatz)
                .orElse("unbekannt");
    }

    public Integer getMengenindex() {
        return hoererService.hoerer(hoerernummer)
                .map(Hoerer::getMengenindex)
                .orElse(-1);
    }

    public String getStrasse() {
        return hoererService.hoerer(hoerernummer)
                .map(Hoerer::getStrasse)
                .orElse("unbekannt");
    }

    public String getPlz() {
        return hoererService.hoerer(hoerernummer)
                .map(Hoerer::getPlz)
                .orElse("unbekannt");
    }

    public String getOrt() {
        return hoererService.hoerer(hoerernummer)
                .map(Hoerer::getOrt)
                .orElse("unbekannt");
    }

    public String getLand() {
        return hoererService.hoerer(hoerernummer)
                .map(Hoerer::getLand)
                .orElse("unbekannt");
    }

    public HoererEmail getEmail() {
        return hoererService.hoerer(hoerernummer)
                .map(Hoerer::getHoereremail)
                .orElse(HoererEmail.UNBEKANNT);
    }

    public boolean hasSperrtermin() {
        return hoererService.hoerer(hoerernummer)
                .filter(h -> null != h.getSperrTerminVon() || null != h.getSperrTerminBis())
                .isPresent();
    }

    public String getSperrTerminVonAufDeutsch() {
        return hoererService.hoerer(hoerernummer)
                .map(h -> h.getSperrTerminVon().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")))
                .orElse("unbekannt");
    }

    public String getSperrTerminBisAufDeutsch() {
        return hoererService.hoerer(hoerernummer)
                .map(h -> h.getSperrTerminBis().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")))
                .orElse("unbekannt");
    }

    public boolean hasUrlaub() {
        return hoererService.hoerer(hoerernummer)
                .filter(h -> null != h.getUrlaubVon() || null != h.getUrlaubBis())
                .isPresent();
    }

    public String getUrlaubVonAufDeutsch() {
        return hoererService.hoerer(hoerernummer)
                .map(h -> h.getUrlaubVon().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")))
                .orElse("unbekannt");
    }

    public String getUrlaubBisAufDeutsch() {
        return hoererService.hoerer(hoerernummer)
                .map(h -> h.getUrlaubBis().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")))
                .orElse("unbekannt");
    }

    public String getUrlaubName2() {
        return hoererService.hoerer(hoerernummer)
                .map(Hoerer::getUrlaubName2)
                .orElse("unbekannt");
    }

    public String getUrlaubAdresszusatz() {
        return hoererService.hoerer(hoerernummer)
                .map(Hoerer::getUrlaubAdresszusatz)
                .orElse("unbekannt");
    }

    public String getUrlaubStrasse() {
        return hoererService.hoerer(hoerernummer)
                .map(Hoerer::getUrlaubStrasse)
                .orElse("unbekannt");
    }

    public String getUrlaubPlz() {
        return hoererService.hoerer(hoerernummer)
                .map(Hoerer::getUrlaubPlz)
                .orElse("unbekannt");
    }

    public String getUrlaubOrt() {
        return hoererService.hoerer(hoerernummer)
                .map(Hoerer::getUrlaubOrt)
                .orElse("unbekannt");
    }

    public String getUrlaubLand() {
        return hoererService.hoerer(hoerernummer)
                .map(Hoerer::getUrlaubLand)
                .orElse("unbekannt");
    }

    public String getRueckbuchungsdatumAufDeutsch() {
        return hoererService.hoerer(hoerernummer)
                .map(h -> h.getRueckbuchungsdatum().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")))
                .orElse("unbekannt");
    }

    public int getAnzahlAufBestellkarte() {
        return cdLieferungService.bestellkarten(hoerernummer).size();
    }

}

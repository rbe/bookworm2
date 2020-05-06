/*
 * Copyright (C) 2011-2019 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchkatalog.ui.nutzerdaten;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;

import wbh.bookworm.hoerbuchkatalog.app.hoerer.HoererService;
import wbh.bookworm.hoerbuchkatalog.app.lieferung.CdLieferungService;
import wbh.bookworm.hoerbuchkatalog.domain.hoerer.HoererEmail;
import wbh.bookworm.hoerbuchkatalog.domain.hoerer.Hoerername;
import wbh.bookworm.hoerbuchkatalog.domain.hoerer.Nachname;
import wbh.bookworm.hoerbuchkatalog.domain.hoerer.Vorname;
import wbh.bookworm.hoerbuchkatalog.ui.katalog.HoererSession;
import wbh.bookworm.shared.domain.hoerer.Hoerernummer;

// TODO ELValueCache für Hoerer und CdLieferung benutzen
@Component
@RequestScope
public class MeineDaten {

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
        return hoererService.hoerer(hoerernummer).getHoerername();
    }

    public Nachname getNachname() {
        return hoererService.hoerer(hoerernummer).getNachname();
    }

    public String getNachnamenszusatz() {
        return hoererService.hoerer(hoerernummer).getNachnamenszusatz();
    }

    public Vorname getVorname() {
        return hoererService.hoerer(hoerernummer).getVorname();
    }

    public String getGeburtsdatumAufDeutsch() {
        return hoererService.hoerer(hoerernummer).getGeburtsdatumAufDeutsch();
    }

    public String getAdresszusatz() {
        return hoererService.hoerer(hoerernummer).getAdresszusatz();
    }

    public Integer getMengenindex() {
        return hoererService.hoerer(hoerernummer).getMengenindex();
    }

    public String getStrasse() {
        return hoererService.hoerer(hoerernummer).getStrasse();
    }

    public String getPlz() {
        return hoererService.hoerer(hoerernummer).getPlz();
    }

    public String getOrt() {
        return hoererService.hoerer(hoerernummer).getOrt();
    }

    public String getLand() {
        return hoererService.hoerer(hoerernummer).getLand();
    }

    public HoererEmail getEmail() {
        return hoererService.hoerer(hoerernummer).getHoereremail();
    }

    public boolean hasSperrtermin() {
        return hoererService.hoerer(hoerernummer).hasSperrtermin();
    }

    public String getSperrTerminVonAufDeutsch() {
        return hoererService.hoerer(hoerernummer).getSperrterminVonAufDeutsch();
    }

    public String getSperrTerminBisAufDeutsch() {
        return hoererService.hoerer(hoerernummer).getSperrterminBisAufDeutsch();
    }

    public boolean hasUrlaub() {
        return hoererService.hoerer(hoerernummer).hasUrlaub();
    }

    public String getUrlaubVonAufDeutsch() {
        return hoererService.hoerer(hoerernummer).getUrlaubVonAufDeutsch();
    }

    public String getUrlaubBisAufDeutsch() {
        return hoererService.hoerer(hoerernummer).getUrlaubBisAufDeutsch();
    }

    public String getUrlaubName2() {
        return hoererService.hoerer(hoerernummer).getUrlaubName2();
    }

    public String getUrlaubAdresszusatz() {
        return hoererService.hoerer(hoerernummer).getUrlaubAdresszusatz();
    }

    public String getUrlaubStrasse() {
        return hoererService.hoerer(hoerernummer).getUrlaubStrasse();
    }

    public String getUrlaubPlz() {
        return hoererService.hoerer(hoerernummer).getUrlaubPlz();
    }

    public String getUrlaubOrt() {
        return hoererService.hoerer(hoerernummer).getUrlaubOrt();
    }

    public String getUrlaubLand() {
        return hoererService.hoerer(hoerernummer).getUrlaubLand();
    }

    public String getRueckbuchungsdatumAufDeutsch() {
        return hoererService.hoerer(hoerernummer).getRueckbuchungsdatumAufDeutsch();
    }

    public String getAnzahlAufBestellkarte() {
        return cdLieferungService.hatBestellkarten()
                ? String.format("%d", cdLieferungService.bestellkarten(hoerernummer).size())
                : "keine Daten vorhanden";
    }

}

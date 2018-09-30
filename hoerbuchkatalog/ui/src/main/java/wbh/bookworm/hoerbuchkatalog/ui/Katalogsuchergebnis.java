/*
 * Copyright (C) 2011-2018 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchkatalog.ui;

import wbh.bookworm.hoerbuchkatalog.app.katalog.HoerbuchkatalogService;
import wbh.bookworm.hoerbuchkatalog.domain.hoerer.Hoerernummer;
import wbh.bookworm.hoerbuchkatalog.domain.katalog.Hoerbuch;
import wbh.bookworm.hoerbuchkatalog.domain.katalog.Suchergebnis;
import wbh.bookworm.hoerbuchkatalog.domain.katalog.Titelnummer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.SessionScope;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

@Component
@SessionScope
public class Katalogsuchergebnis {

    private static final Logger LOGGER = LoggerFactory.getLogger(Katalogsuchergebnis.class);

    private static final int ANZAHL_HOERBUECHER_PRO_SEITE = 25;

    private final Hoerernummer hoerernummer;

    private final Navigation navigation;

    private final HoerbuchkatalogService hoerbuchkatalogService;

    private Suchergebnis suchergebnis;

    private int seitenanzahl;

    private int aktuelleSeitennummer;

    private final Hoerbuchdetail hoerbuchdetail;

    @Autowired
    public Katalogsuchergebnis(final Hoerernummer hoerernummer,
                               final Navigation navigation,
                               final HoerbuchkatalogService hoerbuchkatalogService,
                               final Hoerbuchdetail hoerbuchdetail) {
        this.hoerernummer = hoerernummer;
        this.navigation = navigation;
        this.hoerbuchkatalogService = hoerbuchkatalogService;
        this.hoerbuchdetail = hoerbuchdetail;
        LOGGER.trace("{} initialisiert", this);
    }

    public int getAnzahlHoerbuecherProSeite() {
        LOGGER.trace("");
        return ANZAHL_HOERBUECHER_PRO_SEITE;
    }

    void neuesSuchergebnis(final Suchergebnis suchergebnis) {
        LOGGER.trace("Neues Suchergebnis: {}", suchergebnis);
        leeren();
        this.suchergebnis = suchergebnis;
        seitenanzahl = (int) Math.ceil((double) suchergebnis.getAnzahl() / ANZAHL_HOERBUECHER_PRO_SEITE);
        aktuelleSeitennummer = 1;
    }

    public String getSuchparameterAlsText() {
        LOGGER.trace("");
        return null != suchergebnis ? suchergebnis.getSuchparameter().alsText() : "";
    }

    public int getAnzahl() {
        LOGGER.trace("");
        return null != suchergebnis ? suchergebnis.getAnzahl() : 0;
    }

    public int getSeitenanzahl() {
        LOGGER.trace("Insgesamt {} Seiten", seitenanzahl);
        return seitenanzahl;
    }

    public int getAktuelleSeitennummer() {
        LOGGER.trace("Aktuelle Seitennummer ist {}", aktuelleSeitennummer);
        return aktuelleSeitennummer;
    }

    public List<Hoerbuch> getAktuelleSeite() {
        LOGGER.trace("");
        if (null == suchergebnis) {
            return Collections.emptyList();
        }
        final int skip = (aktuelleSeitennummer - 1) * ANZAHL_HOERBUECHER_PRO_SEITE;
        final Titelnummer[] titelnummernAktuelleSeite =
                suchergebnis.getTitelnummern().stream()
                        .skip(skip)
                        .limit(ANZAHL_HOERBUECHER_PRO_SEITE)
                        .toArray(Titelnummer[]::new);
        LOGGER.debug("Index von {} bis {} ({} pro Seite), Titelnummern={}",
                skip, skip + ANZAHL_HOERBUECHER_PRO_SEITE, ANZAHL_HOERBUECHER_PRO_SEITE,
                titelnummernAktuelleSeite);
        return hoerbuchkatalogService.hole(hoerernummer, titelnummernAktuelleSeite);

    }

    public boolean isVorherigeSeiteVorhanden() {
        LOGGER.trace("");
        return aktuelleSeitennummer - 1 > 0;
    }

    public String vorherigeSeite() {
        LOGGER.trace("");
        aktuelleSeitennummer--;
        if (aktuelleSeitennummer < 0) {
            aktuelleSeitennummer = seitenanzahl;
        }
        return navigation.zumSuchergebnis();
    }

    public boolean isNaechsteSeiteVorhanden() {
        LOGGER.trace("");
        return aktuelleSeitennummer + 1 <= seitenanzahl;
    }

    public String naechsteSeite() {
        LOGGER.trace("");
        aktuelleSeitennummer++;
        if (aktuelleSeitennummer > seitenanzahl) {
            aktuelleSeitennummer = 1;
        }
        return navigation.zumSuchergebnis();
    }

    public String ansehen(final Titelnummer titelnummer) {
        LOGGER.trace("");
        hoerbuchdetail.setTitelnummer(titelnummer);
        return navigation.zumHoerbuchdetail();
    }

    public int position(final Titelnummer titelnummer) {
        final int size = suchergebnis.getTitelnummern().size();
        final Iterator<Titelnummer> iterator = suchergebnis.getTitelnummern().iterator();
        for (int i = 0; i < size; i++) {
            final Titelnummer next = iterator.next();
            if (next.equals(titelnummer)) {
                return i + 1;
            }
        }
        return 0;
    }

    public boolean vorherigesBuchVorhanden(final Titelnummer titelnummer) {
        if (null == titelnummer) {
            return false;
        }
        return suchergebnis.vorherigeVorhanden(titelnummer);
    }

    public String vorherigesHoerbuchAnsehen(final Titelnummer titelnummer) {
        final Titelnummer vorherige = suchergebnis.vorherige(titelnummer);
        hoerbuchdetail.setTitelnummer(vorherige);
        return navigation.zumHoerbuchdetail();
    }

    public boolean naechstesBuchVorhanden(final Titelnummer titelnummer) {
        if (null == titelnummer) {
            return false;
        }
        return suchergebnis.naechsteVorhanden(titelnummer);
    }

    public String naechstesHoerbuchAnsehen(final Titelnummer titelnummer) {
        final Titelnummer naechste = suchergebnis.naechste(titelnummer);
        hoerbuchdetail.setTitelnummer(naechste);
        return navigation.zumHoerbuchdetail();
    }

    void leeren() {
        LOGGER.trace("{} wird geleert", this);
        suchergebnis = null;
        seitenanzahl = 0;
        aktuelleSeitennummer = 0;
        hoerbuchdetail.leeren();
    }

}

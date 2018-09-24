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
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

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
    }

    public int getAnzahlHoerbuecherProSeite() {
        return ANZAHL_HOERBUECHER_PRO_SEITE;
    }

    void neuesSuchergebnis(final Suchergebnis suchergebnis) {
        this.suchergebnis = suchergebnis;
        seitenanzahl = (int) Math.ceil((double) suchergebnis.getAnzahl() / ANZAHL_HOERBUECHER_PRO_SEITE);
        aktuelleSeitennummer = 1;
    }

    public String getSuchparameterAlsText() {
        return null != suchergebnis ? suchergebnis.getSuchparameter().getLabel() : "";
    }

    public int getAnzahl() {
        return null != suchergebnis ? suchergebnis.getAnzahl() : 0;
    }

    public int getSeitenanzahl() {
        return seitenanzahl;
    }

    public int getAktuelleSeitennummer() {
        return aktuelleSeitennummer;
    }

    public List<Hoerbuch> getAktuelleSeite() {
        if (null == suchergebnis) {
            return Collections.emptyList();
        }
        final int skip = (aktuelleSeitennummer - 1) * ANZAHL_HOERBUECHER_PRO_SEITE;
        LOGGER.debug("Index von {} +{} bis {}",
                skip, ANZAHL_HOERBUECHER_PRO_SEITE, skip + ANZAHL_HOERBUECHER_PRO_SEITE);
        return suchergebnis.getTitelnummern().stream()
                .skip(skip)
                .limit(ANZAHL_HOERBUECHER_PRO_SEITE)
                .map((Titelnummer titelnummer) -> hoerbuchkatalogService.finde(hoerernummer, titelnummer))
                .collect(Collectors.toCollection(LinkedList::new));

    }

    public boolean isVorherigeSeiteVorhanden() {
        return aktuelleSeitennummer - 1 > 0;
    }

    public String vorherigeSeite() {
        aktuelleSeitennummer--;
        if (aktuelleSeitennummer < 0) {
            aktuelleSeitennummer = seitenanzahl;
        }
        return navigation.suchergebnis();
    }

    public boolean isNaechsteSeiteVorhanden() {
        return aktuelleSeitennummer + 1 <= seitenanzahl;
    }

    public String naechsteSeite() {
        aktuelleSeitennummer++;
        if (aktuelleSeitennummer > seitenanzahl) {
            aktuelleSeitennummer = 1;
        }
        return navigation.suchergebnis();
    }

    public String ansehen(final Hoerbuch hoerbuch) {
        hoerbuchdetail.setHoerbuch(hoerbuch);
        return navigation.hoerbuchdetail();
    }

    public int position(final Hoerbuch hoerbuch) {
        final int size = suchergebnis.getTitelnummern().size();
        final Iterator<Titelnummer> iterator = suchergebnis.getTitelnummern().iterator();
        for (int i = 0; i < size; i++) {
            final Titelnummer next = iterator.next();
            if (next.equals(hoerbuch.getTitelnummer())) {
                return i + 1;
            }
        }
        return 0;
    }

    public boolean vorherigesBuchVorhanden(final Hoerbuch hoerbuch) {
        if (null == hoerbuch) return false;
        return suchergebnis.vorherigeVorhanden(hoerbuch.getTitelnummer());
    }

    public String vorherigesHoerbuchAnsehen(final Hoerbuch hoerbuch) {
        final Titelnummer vorherige = suchergebnis.vorherige(hoerbuch.getTitelnummer());
        final Hoerbuch vorherigesHoerbuch = hoerbuchkatalogService.finde(hoerernummer, vorherige);
        hoerbuchdetail.setHoerbuch(vorherigesHoerbuch);
        return navigation.hoerbuchdetail();
    }

    public boolean naechstesBuchVorhanden(final Hoerbuch hoerbuch) {
        if (null == hoerbuch) return false;
        return suchergebnis.naechsteVorhanden(hoerbuch.getTitelnummer());
    }

    public String naechstesHoerbuchAnsehen(final Hoerbuch hoerbuch) {
        final Titelnummer naechste = suchergebnis.naechste(hoerbuch.getTitelnummer());
        final Hoerbuch naechstesHoerbuch = hoerbuchkatalogService.finde(hoerernummer, naechste);
        hoerbuchdetail.setHoerbuch(naechstesHoerbuch);
        return navigation.hoerbuchdetail();
    }

    void leeren() {
        suchergebnis = null;
        seitenanzahl = 0;
        aktuelleSeitennummer = 0;
        hoerbuchdetail.setHoerbuch(null);
    }

}

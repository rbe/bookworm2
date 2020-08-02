/*
 * Copyright (C) 2011-2019 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchkatalog.ui.katalog;

import java.io.Serializable;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import wbh.bookworm.hoerbuchkatalog.app.katalog.HoerbuchkatalogService;
import wbh.bookworm.hoerbuchkatalog.domain.katalog.Hoerbuch;
import wbh.bookworm.hoerbuchkatalog.domain.katalog.Suchergebnis;
import wbh.bookworm.shared.domain.hoerbuch.Titelnummer;
import wbh.bookworm.shared.domain.mandant.Hoerernummer;

public final class AbstractSuche<T> implements Serializable {

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractSuche.class);

    // TODO Konfiguration
    private static final int ANZAHL_HOERBUECHER_PRO_SEITE = 25;

    private final transient HoerbuchkatalogService hoerbuchkatalogService;

    private final Hoerernummer hoerernummer;

    private Suchergebnis suchergebnis;

    private int seitenanzahl;

    private int aktuelleSeitennummer;

    public AbstractSuche(final Hoerernummer hoerernummer,
                         final HoerbuchkatalogService hoerbuchkatalogService) {
        LOGGER.trace("Initialisiere {}", this);
        this.hoerernummer = hoerernummer;
        this.hoerbuchkatalogService = hoerbuchkatalogService;
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
        return null != suchergebnis ? suchergebnis.getSuchparameter().alsText() : "";
    }

    public int getAnzahl() {
        return null != suchergebnis ? suchergebnis.getAnzahl() : 0;
    }

    public int getGesamtAnzahlTreffer() {
        return null != suchergebnis ? suchergebnis.getGesamtAnzahlTreffer() : 0;
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

    public void vorherigeSeite() {
        LOGGER.trace("");
        aktuelleSeitennummer--;
        if (aktuelleSeitennummer < 0) {
            aktuelleSeitennummer = seitenanzahl;
        }
    }

    public boolean isNaechsteSeiteVorhanden() {
        LOGGER.trace("");
        return aktuelleSeitennummer + 1 <= seitenanzahl;
    }

    public void naechsteSeite() {
        LOGGER.trace("");
        aktuelleSeitennummer++;
        if (aktuelleSeitennummer > seitenanzahl) {
            aktuelleSeitennummer = 1;
        }
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

    public boolean naechstesBuchVorhanden(final Titelnummer titelnummer) {
        if (null == titelnummer) {
            return false;
        }
        return suchergebnis.naechsteVorhanden(titelnummer);
    }

    @Override
    public String toString() {
        return String.format("AbstractSuche{hoerernummer=%s, suchergebnis=%s, seitenanzahl=%d, aktuelleSeitennummer=%d}",
                hoerernummer, suchergebnis, seitenanzahl, aktuelleSeitennummer);
    }

    void leeren() {
        LOGGER.trace("{} wird geleert", this);
        suchergebnis = null;
        seitenanzahl = 0;
        aktuelleSeitennummer = 0;
    }

}

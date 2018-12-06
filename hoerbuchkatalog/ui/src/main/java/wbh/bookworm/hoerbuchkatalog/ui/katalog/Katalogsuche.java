/*
 * Copyright (C) 2011-2018 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchkatalog.ui.katalog;

import wbh.bookworm.hoerbuchkatalog.app.katalog.HoerbuchkatalogService;
import wbh.bookworm.hoerbuchkatalog.domain.hoerer.Hoerernummer;
import wbh.bookworm.hoerbuchkatalog.domain.katalog.Sachgebiet;
import wbh.bookworm.hoerbuchkatalog.domain.katalog.Suchergebnis;
import wbh.bookworm.hoerbuchkatalog.domain.katalog.Suchparameter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.SessionScope;

import java.io.Serializable;

@Component
@SessionScope
public class Katalogsuche implements Serializable {

    private static final Logger LOGGER = LoggerFactory.getLogger(Katalogsuche.class);

    private final Hoerernummer hoerernummer;

    private final transient HoerbuchkatalogService hoerbuchkatalogService;

    private final Katalogsuchergebnis katalogsuchergebnis;

    private String stichwort;

    private Suchparameter suchparameter;

    @Autowired
    public Katalogsuche(final HoererSession hoererSession,
                        final HoerbuchkatalogService hoerbuchkatalogService,
                        final Katalogsuchergebnis katalogsuchergebnis) {
        this.hoerernummer = hoererSession.getHoerernummer();
        this.hoerbuchkatalogService = hoerbuchkatalogService;
        this.katalogsuchergebnis = katalogsuchergebnis;
        this.suchparameter = new Suchparameter();
    }

    public String getStichwort() {
        //return suchparameter.wert(Suchparameter.Feld.STICHWORT);
        return stichwort;
    }

    public void setStichwort(final String stichwort) {
        //suchparameter.hinzufuegen(Suchparameter.Feld.STICHWORT, stichwort);
        this.stichwort = stichwort;
    }

    public Sachgebiet[] getSachgebiete() {
        return Sachgebiet.values();
    }

    public String getSachgebiet() {
        return suchparameter.wert(Suchparameter.Feld.SACHGEBIET);
    }

    public void setSachgebiet(final String sachgebiet) {
        suchparameter.hinzufuegen(Suchparameter.Feld.SACHGEBIET, sachgebiet);
    }

    public String getAutor() {
        return suchparameter.wert(Suchparameter.Feld.AUTOR);
    }

    public void setAutor(final String autor) {
        suchparameter.hinzufuegen(Suchparameter.Feld.AUTOR, autor);
    }

    public String getTitel() {
        return suchparameter.wert(Suchparameter.Feld.TITEL);
    }

    public void setTitel(final String titel) {
        suchparameter.hinzufuegen(Suchparameter.Feld.TITEL, titel);
    }

    public String getSprecher() {
        return suchparameter.wert(Suchparameter.Feld.SPRECHER);
    }

    public void setSprecher(final String sprecher) {
        suchparameter.hinzufuegen(Suchparameter.Feld.SPRECHER, sprecher);
    }

    public String getEinstelldatum() {
        return suchparameter.wert(Suchparameter.Feld.EINSTELLDATUM);
    }

    public void setEinstelldatum(final String einstelldatum) {
        suchparameter.hinzufuegen(Suchparameter.Feld.EINSTELLDATUM, einstelldatum);
    }

    public String sucheNachStichwort() {
        LOGGER.info("Suche nach Stichwort '{}'", stichwort);
        final Suchergebnis suchergebnis =
                hoerbuchkatalogService.sucheNachStichwort(hoerernummer, stichwort);
        if (suchergebnis.getAnzahl() > 0) {
            katalogsuchergebnis.neuesSuchergebnis(suchergebnis);
            return Navigation.NAV_SUCHERGEBNIS;
        } else {
            return Navigation.NAV_KEINE_SUCHERGEBNISSE;
        }
    }

    public String suchen() {
        LOGGER.info("Suche '{}'", suchparameter);
        final Suchergebnis suchergebnis =
                hoerbuchkatalogService.suchen(hoerernummer, suchparameter);
        if (suchergebnis.getAnzahl() > 0) {
            katalogsuchergebnis.neuesSuchergebnis(suchergebnis);
            return Navigation.NAV_SUCHERGEBNIS;
        } else {
            return Navigation.NAV_KEINE_SUCHERGEBNISSE;
        }
    }

    void leeren() {
        suchparameter = new Suchparameter();
    }

}

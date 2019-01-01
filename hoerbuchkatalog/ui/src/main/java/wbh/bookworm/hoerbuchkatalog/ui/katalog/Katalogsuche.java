/*
 * Copyright (C) 2011-2018 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchkatalog.ui.katalog;

import wbh.bookworm.hoerbuchkatalog.app.katalog.HoerbuchkatalogService;
import wbh.bookworm.hoerbuchkatalog.domain.katalog.Sachgebiet;
import wbh.bookworm.hoerbuchkatalog.domain.katalog.Suchergebnis;
import wbh.bookworm.hoerbuchkatalog.domain.katalog.Suchparameter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;

@Component
@RequestScope
public class Katalogsuche {

    private static final Logger LOGGER = LoggerFactory.getLogger(Katalogsuche.class);

    private final HoererSession hoererSession;

    private final HoerbuchkatalogService hoerbuchkatalogService;

    private final Katalogsuchergebnis katalogsuchergebnis;

    private String stichwort;

    @Autowired
    public Katalogsuche(final HoererSession hoererSession,
                        final HoerbuchkatalogService hoerbuchkatalogService,
                        final Katalogsuchergebnis katalogsuchergebnis) {
        this.hoererSession = hoererSession;
        this.hoerbuchkatalogService = hoerbuchkatalogService;
        this.katalogsuchergebnis = katalogsuchergebnis;
    }

    public String getStichwort() {
        //return hoererSession.wertDesSuchparameters(Suchparameter.Feld.STICHWORT);
        return stichwort;
    }

    public void setStichwort(final String stichwort) {
        //hoererSession.suchparameterHinzufuegen(Suchparameter.Feld.STICHWORT, stichwort);
        this.stichwort = stichwort;
    }

    public Sachgebiet[] getSachgebiete() {
        return Sachgebiet.values();
    }

    public String getSachgebiet() {
        return hoererSession.wertDesSuchparameters(Suchparameter.Feld.SACHGEBIET);
    }

    public void setSachgebiet(final String sachgebiet) {
        hoererSession.suchparameterHinzufuegen(Suchparameter.Feld.SACHGEBIET, sachgebiet);
    }

    public String getAutor() {
        return hoererSession.wertDesSuchparameters(Suchparameter.Feld.AUTOR);
    }

    public void setAutor(final String autor) {
        hoererSession.suchparameterHinzufuegen(Suchparameter.Feld.AUTOR, autor);
    }

    public String getTitel() {
        return hoererSession.wertDesSuchparameters(Suchparameter.Feld.TITEL);
    }

    public void setTitel(final String titel) {
        hoererSession.suchparameterHinzufuegen(Suchparameter.Feld.TITEL, titel);
    }

    public String getSprecher() {
        return hoererSession.wertDesSuchparameters(Suchparameter.Feld.SPRECHER);
    }

    public void setSprecher(final String sprecher) {
        hoererSession.suchparameterHinzufuegen(Suchparameter.Feld.SPRECHER, sprecher);
    }

    public String getEinstelldatum() {
        return hoererSession.wertDesSuchparameters(Suchparameter.Feld.EINSTELLDATUM);
    }

    public void setEinstelldatum(final String einstelldatum) {
        hoererSession.suchparameterHinzufuegen(Suchparameter.Feld.EINSTELLDATUM, einstelldatum);
    }

    public String sucheNachStichwort() {
        LOGGER.trace("Suche nach Stichwort '{}' starten", stichwort);
        final Suchergebnis suchergebnis = hoerbuchkatalogService.sucheNachStichwort(
                hoererSession.getHoerernummer(), stichwort);
        if (suchergebnis.getAnzahl() > 0) {
            LOGGER.debug("Zeige {} Suchergebnisse an", suchergebnis.getAnzahl());
            katalogsuchergebnis.neuesSuchergebnis(suchergebnis);
            return Navigation.NAV_SUCHERGEBNIS;
        } else {
            LOGGER.debug("Zeige Seite für keine Suchergebnisse an");
            return Navigation.NAV_KEINE_SUCHERGEBNISSE;
        }
    }

    public String suchen() {
        LOGGER.trace("Suche mit '{}' starten", hoererSession.getSuchparameter());
        final Suchergebnis suchergebnis = hoerbuchkatalogService.suchen(
                hoererSession.getHoerernummer(), hoererSession.getSuchparameter());
        if (suchergebnis.getAnzahl() > 0) {
            katalogsuchergebnis.neuesSuchergebnis(suchergebnis);
            return Navigation.NAV_SUCHERGEBNIS;
        } else {
            return Navigation.NAV_KEINE_SUCHERGEBNISSE;
        }
    }

    void leeren() {
        hoererSession.suchparameterVergessen();
    }

}

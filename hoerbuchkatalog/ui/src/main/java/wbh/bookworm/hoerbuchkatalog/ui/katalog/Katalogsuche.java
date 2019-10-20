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

import javax.faces.component.UIComponent;

@Component
@RequestScope
public class Katalogsuche {

    private static final Logger LOGGER = LoggerFactory.getLogger(Katalogsuche.class);

    private final HoererSession hoererSession;

    private final HoerbuchkatalogService hoerbuchkatalogService;

    private final Katalogsuchergebnis katalogsuchergebnis;

    @Autowired
    public Katalogsuche(final HoererSession hoererSession,
                        final HoerbuchkatalogService hoerbuchkatalogService,
                        final Katalogsuchergebnis katalogsuchergebnis) {
        this.hoererSession = hoererSession;
        this.hoerbuchkatalogService = hoerbuchkatalogService;
        this.katalogsuchergebnis = katalogsuchergebnis;
    }

    public String getStichwort() {
        return hoererSession.wertDesSuchparameters(Suchparameter.Feld.STICHWORT);
    }

    public void setStichwort(final String stichwort) {
        hoererSession.suchparameterHinzufuegen(Suchparameter.Feld.STICHWORT, stichwort);
    }

    public Sachgebiet[] getSachgebiete() {
        return Sachgebiet.values();
    }

    public String getSpSachgebiet() {
        return hoererSession.wertDesSuchparameters(Suchparameter.Feld.SACHGEBIET);
    }

    public void setSpSachgebiet(final String sachgebiet) {
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
        return hoererSession.wertDesSuchparameters(Suchparameter.Feld.SPRECHER1);
    }

    public void setSprecher(final String sprecher) {
        hoererSession.suchparameterHinzufuegen(Suchparameter.Feld.SPRECHER1, sprecher);
    }

    public String getEinstelldatum() {
        return hoererSession.wertDesSuchparameters(Suchparameter.Feld.EINSTELLDATUM);
    }

    public void setEinstelldatum(final String einstelldatum) {
        hoererSession.suchparameterHinzufuegen(Suchparameter.Feld.EINSTELLDATUM, einstelldatum);
    }

    private UIComponent sachgebietUiComponent;
    public void setSachgebiet(final UIComponent sachgebietUiComponent) {
        this.sachgebietUiComponent = sachgebietUiComponent;
    }
    public UIComponent getSachgebiet() {return sachgebietUiComponent;}

    public String suchen() {
        LOGGER.trace("Suche mit '{}' starten", hoererSession.getSuchparameter());
        final Suchergebnis suchergebnis = hoerbuchkatalogService.suchen(
                hoererSession.getHoerernummer(), hoererSession.getSuchparameter());
        if (suchergebnis.hatErgebnisse()) {
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

/*
 * Copyright (C) 2011-2018 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchkatalog.ui;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.SessionScope;

import javax.faces.context.FacesContext;

@Component
@SessionScope
public class Navigation {

    static final String NAV_SUCHE = "suche.xhtml";

    static final String NAV_SUCHERGEBNIS = "suchergebnis.xhtml";

    static final String NAV_KEINE_SUCHERGEBNISSE = "keine-suchergebnisse.xhtml";

    static final String NAV_WARENKORB = "warenkorb.xhtml";

    private final Katalogsuche katalogsuche;

    private final Katalogsuchergebnis katalogsuchergebnis;

    private final Bestellung bestellung;

    @Autowired
    public Navigation(final Katalogsuche katalogsuche,
                      final Katalogsuchergebnis katalogsuchergebnis,
                      final Bestellung bestellung) {
        this.katalogsuche = katalogsuche;
        this.katalogsuchergebnis = katalogsuchergebnis;
        this.bestellung = bestellung;
    }

    private String getViewId() {
        return FacesContext.getCurrentInstance().getViewRoot().getViewId();
    }

    public boolean isLinkZurSucheAnzeigen() {
        final String viewId = getViewId();
        return katalogsuchergebnis.getAnzahl() == 0
                && !viewId.contains(Navigation.NAV_SUCHE);
    }

    public String suche() {
        return NAV_SUCHE;
    }

    public boolean isLinkZumSuchergebnisAnzeigen() {
        final String viewId = getViewId();
        return katalogsuchergebnis.getAnzahl() > 0
                && !viewId.contains(Navigation.NAV_SUCHERGEBNIS);
    }

    public boolean isLinkZurErneutenSucheAnzeigen() {
        final String viewId = getViewId();
        return katalogsuchergebnis.getAnzahl() > 0
                && !viewId.contains(Navigation.NAV_SUCHE);
    }

    public String suchergebnis() {
        return NAV_SUCHERGEBNIS;
    }

    public String erneuteSuche() {
        katalogsuche.leeren();
        katalogsuchergebnis.leeren();
        return NAV_SUCHE;
    }

    public boolean isLinkZumWarenkorbAnzeigen() {
        return bestellung.getAnzahl() > 0;
    }

    public String getWarenkorb() {
        return NAV_WARENKORB;
    }

}

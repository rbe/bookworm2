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

    static final String NAV_SUCHE = "katalogsuche.xhtml?faces-redirect=true";

    static final String NAV_SUCHERGEBNIS = "katalogsuchergebnis.xhtml?faces-redirect=true";

    static final String NAV_HOERBUCHDETAIL = "hoerbuchdetail.xhtml?faces-redirect=true";

    static final String NAV_KEINE_SUCHERGEBNISSE = "keine-suchergebnisse.xhtml?faces-redirect=true";

    static final String NAV_MERKLISTE = "meinemerkliste.xhtml?faces-redirect=true";

    static final String NAV_WARENKORB = "meinwarenkorb.xhtml?faces-redirect=true";

    private static final String NAV_BESTELLUNG_ERFOLGREICH = "bestellung-erfolgreich.xhtml?faces-redirect=true";

    private final Katalogsuche katalogsuche;

    private final Katalogsuchergebnis katalogsuchergebnis;

    private final MeineMerkliste meineMerkliste;

    private final MeinWarenkorb meinWarenkorb;

    @Autowired
    public Navigation(final Katalogsuche katalogsuche,
                      final Katalogsuchergebnis katalogsuchergebnis,
                      final MeineMerkliste meineMerkliste,
                      final MeinWarenkorb meinWarenkorb) {
        this.katalogsuche = katalogsuche;
        this.katalogsuchergebnis = katalogsuchergebnis;
        this.meineMerkliste = meineMerkliste;
        this.meinWarenkorb = meinWarenkorb;
    }

    private String getViewId() {
        return FacesContext.getCurrentInstance().getViewRoot().getViewId();
    }

    public boolean isLinkZurSucheAnzeigen() {
        final String viewId = getViewId();
        return katalogsuchergebnis.getAnzahl() == 0
                && !viewId.contains(Navigation.NAV_SUCHE);
    }

    public String zurSuche() {
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

    public String zumSuchergebnis() {
        return NAV_SUCHERGEBNIS;
    }

    public String zurErneutenSuche() {
        katalogsuche.leeren();
        katalogsuchergebnis.leeren();
        return NAV_SUCHE;
    }

    public String zumHoerbuchdetail() {
        return NAV_HOERBUCHDETAIL;
    }

    public boolean isLinkZurMerklisteAnzeigen() {
        return meineMerkliste.getAnzahl() > 0;
    }

    public String zuMeinerMerkliste() {
        return NAV_MERKLISTE;
    }

    public boolean isLinkZumWarenkorbAnzeigen() {
        return meinWarenkorb.getAnzahl() > 0;
    }

    public String zuMeinemWarenkorb() {
        return NAV_WARENKORB;
    }

    public String zuBestellungErfolgreich() {
        return NAV_BESTELLUNG_ERFOLGREICH;
    }

}

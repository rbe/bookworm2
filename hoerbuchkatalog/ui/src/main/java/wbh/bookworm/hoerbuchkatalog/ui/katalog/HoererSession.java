/*
 * Copyright (C) 2011-2018 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchkatalog.ui.katalog;

import wbh.bookworm.hoerbuchkatalog.app.bestellung.BestellungSessionId;
import wbh.bookworm.hoerbuchkatalog.domain.hoerer.Hoerer;
import wbh.bookworm.hoerbuchkatalog.domain.hoerer.HoererEmail;
import wbh.bookworm.hoerbuchkatalog.domain.hoerer.Hoerername;
import wbh.bookworm.hoerbuchkatalog.domain.hoerer.Hoerernummer;
import wbh.bookworm.hoerbuchkatalog.domain.hoerer.Nachname;
import wbh.bookworm.hoerbuchkatalog.domain.hoerer.Vorname;
import wbh.bookworm.hoerbuchkatalog.ui.http.SessionKey;

import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.SessionScope;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.Serializable;

@Component
@SessionScope
public class HoererSession implements Serializable {

    public HoererSession() {
        getSession().setAttribute(SessionKey.HOERER,
                /* TODO Daten aus Nutzerbereich abfragen */new Hoerer(
                        (Hoerernummer) getSession().getAttribute(SessionKey.HOERERNUMMER),
                        new Hoerername(new Vorname("VORNAME"), new Nachname("NACHNAME")),
                        new HoererEmail("hoerer@example.com")));
        getSession().setAttribute(SessionKey.BESTELLUNG_SESSION_ID,
                new BestellungSessionId(getHoerernummer().getValue(), getSession().getId()));
    }

    public Hoerernummer getHoerernummer() {
        return getHoerer().getHoerernummer();
    }

    public Hoerer getHoerer() {
        return (Hoerer) getSession().getAttribute(SessionKey.HOERER);
    }

    public Hoerername getHoerername() {
        return getHoerer().getHoerername();
    }

    public boolean isHoererIstBekannt() {
        return getHoerernummer().isBekannt();
    }

    public boolean isHoererIstUnbekannt() {
        return getHoerernummer().isUnbekannt();
    }

    BestellungSessionId getBestellungSessionId() {
        return (BestellungSessionId) getSession().getAttribute(SessionKey.BESTELLUNG_SESSION_ID);
    }

    private HttpSession getSession() {
        final ExternalContext externalContext = FacesContext.getCurrentInstance()
                .getExternalContext();
        final HttpServletRequest request = (HttpServletRequest) externalContext
                .getRequest();
        return request.getSession(false);
    }

}

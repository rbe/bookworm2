/*
 * Copyright (C) 2011-2018 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchkatalog.ui;

import wbh.bookworm.hoerbuchkatalog.domain.hoerer.Hoerer;
import wbh.bookworm.hoerbuchkatalog.domain.hoerer.HoererEmail;
import wbh.bookworm.hoerbuchkatalog.domain.hoerer.Hoerername;
import wbh.bookworm.hoerbuchkatalog.domain.hoerer.Hoerernummer;
import wbh.bookworm.hoerbuchkatalog.domain.hoerer.Nachname;
import wbh.bookworm.hoerbuchkatalog.domain.hoerer.Vorname;

import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.SessionScope;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.Serializable;

@Component
@SessionScope
class HoererSession implements Serializable {

    private static final String HOERERNUMMER = "hnr";

    public Hoerernummer hoerernummer() {
        return (Hoerernummer) getSession().getAttribute(HOERERNUMMER);
    }

    public Hoerer hoerer() {
        return new Hoerer(hoerernummer(),
                new Hoerername(new Vorname("VORNAME"), new Nachname("NACHNAME")),
                new HoererEmail("hoerer@example.com"));
    }

    public boolean isHoererIstBekannt() {
        return hoerernummer().isBekannt();
    }

    public boolean isHoererIstUnbekannt() {
        return hoerernummer().isUnbekannt();
    }

    private HttpSession getSession() {
        final ExternalContext externalContext = FacesContext.getCurrentInstance()
                .getExternalContext();
        final HttpServletRequest request = (HttpServletRequest) externalContext
                .getRequest();
        return request.getSession(false);
    }

}

/*
 * Copyright (C) 2011-2018 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchkatalog.ui;

import wbh.bookworm.hoerbuchkatalog.domain.hoerer.Hoerernummer;

import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.SessionScope;

import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.Serializable;

@Component
@SessionScope
public class HoererSession implements Serializable {

    private static final String HOERERNUMMER = "hnr";

    public Hoerernummer getHoerernummer() {
        return (Hoerernummer) getSession().getAttribute(HOERERNUMMER);
    }

    private HttpSession getSession() {
        return ((HttpServletRequest) FacesContext.getCurrentInstance()
                .getExternalContext()
                .getRequest())
                .getSession();
    }

}

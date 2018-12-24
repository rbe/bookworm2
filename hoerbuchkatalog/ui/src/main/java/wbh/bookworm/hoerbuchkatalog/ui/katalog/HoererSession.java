/*
 * Copyright (C) 2011-2018 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchkatalog.ui.katalog;

import wbh.bookworm.hoerbuchkatalog.app.bestellung.BestellungSessionId;
import wbh.bookworm.hoerbuchkatalog.app.katalog.HoerbuchkatalogService;
import wbh.bookworm.hoerbuchkatalog.domain.hoerer.Hoerer;
import wbh.bookworm.hoerbuchkatalog.domain.hoerer.HoererEmail;
import wbh.bookworm.hoerbuchkatalog.domain.hoerer.Hoerername;
import wbh.bookworm.hoerbuchkatalog.domain.hoerer.Hoerernummer;
import wbh.bookworm.hoerbuchkatalog.domain.hoerer.Nachname;
import wbh.bookworm.hoerbuchkatalog.domain.hoerer.Vorname;
import wbh.bookworm.hoerbuchkatalog.domain.katalog.Hoerbuch;
import wbh.bookworm.hoerbuchkatalog.domain.katalog.Titelnummer;

import aoc.tools.RandomStringGenerator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.SessionScope;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.Serializable;
import java.util.Objects;

// TODO Alle JSF Beans: Session -> RequestScope, Speicherung von Daten nur in der Session
/* TODO Beim Hörbuchkatalog als Observer registrieren,
 * TODO damit Änderungen am Katalog die hier gespeichertern Informationen beeinflussen */
@Component
@SessionScope
public class HoererSession implements Serializable {

    private static final transient Logger LOGGER = LoggerFactory.getLogger(HoererSession.class);

    private final transient HoerbuchkatalogService hoerbuchkatalogService;

    private Hoerer hoerer;

    private BestellungSessionId bestellungSessionId;

    private Hoerbuch hoerbuch;

    @Autowired
    public HoererSession(final HttpServletRequest httpServletRequest,
                         final HoerbuchkatalogService hoerbuchkatalogService) {
        this.hoerbuchkatalogService = hoerbuchkatalogService;
        hoererSetzen(Hoerernummer.UNBEKANNT);
        final HttpSession session = httpServletRequest.getSession(false);
        bestellungSessionId = new BestellungSessionId(getHoerernummer().getValue(),
                null != session
                        ? session.getId()
                        : RandomStringGenerator.next());
    }

    void hoererSetzen(final Hoerernummer hoerernummer) {
        Objects.requireNonNull(hoerernummer);
        // Nur (einmal) erlauben, wenn kein oder unbekannter Hörer
        if (null == hoerer || hoerer.isUnbekannt()) {
            hoerer = /* TODO Daten aus Nutzerbereich abfragen oder per Request übergeben? */
                    new Hoerer(hoerernummer,
                            new Hoerername(new Vorname(""), new Nachname("")),
                            new HoererEmail("hoerer@example.com"));
        } else {
            LOGGER.warn("Bekannte Hörernummer {} bereits in der Session gesetzt!", hoerernummer);
        }
    }

    public Hoerernummer getHoerernummer() {
        return hoerer.getHoerernummer();
    }

    public boolean isHoererIstBekannt() {
        return hoerer.isBekannt();
    }

    public boolean isHoererIstUnbekannt() {
        return hoerer.isUnbekannt();
    }

    boolean hasHoerername() {
        return hoerer.hasHoerername();
    }

    public Hoerername getHoerername() {
        return hoerer.getHoerername();
    }

    boolean hasHoereremail() {
        return hoerer.hasHoereremail();
    }

    HoererEmail getHoereremail() {
        return hoerer.getHoereremail();
    }

    BestellungSessionId getBestellungSessionId() {
        return bestellungSessionId;
    }

    void hoerbuchMerken(final Titelnummer titelnummer) {
        hoerbuch = hoerbuchkatalogService.hole(getHoerernummer(), titelnummer);
    }

    Hoerbuch hoerbuch() {
        return hoerbuch;
    }

    void hoerbuchVergessen() {
        hoerbuch = null;
    }

    /*
    private HttpSession session() {
        final ExternalContext externalContext = FacesContext.getCurrentInstance()
                .getExternalContext();
        final HttpServletRequest request = (HttpServletRequest) externalContext
                .getRequest();
        return request.getSession(false);
    }
    */

}

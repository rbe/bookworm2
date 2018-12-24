/*
 * Copyright (C) 2011-2018 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchkatalog.ui.katalog;

import wbh.bookworm.hoerbuchkatalog.domain.hoerer.Hoerernummer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Date;

@WebFilter(servletNames = "FacesServlet", urlPatterns = {"*.xhtml", "logout"})
public class HoerernummerFilter implements Filter {

    private static final Logger LOGGER = LoggerFactory.getLogger(HoerernummerFilter.class);

    @Override
    public void doFilter(final ServletRequest req, final ServletResponse res,
                         final FilterChain chain) throws IOException, ServletException {
        final HttpServletRequest request = (HttpServletRequest) req;
        if (request.getRequestURI().endsWith("logout")) {
            logoutVerarbeiten(request);
        } else {
            LOGGER.trace("Suche Hoerernummer in HTTP-Anfrage '{}'", request.getRequestURI());
            final HttpSession session = request.getSession(false);
            if (null != session) {
                LOGGER.debug("Session {} ist {}, lastAccessedTime={}",
                        session.getId(), session.isNew() ? "neu" : "nicht neu",
                        new Date(session.getLastAccessedTime()));
                final Hoerernummer sessionHnr =
                        (Hoerernummer) session.getAttribute(SessionKey.HOERERNUMMER);
                if (null == sessionHnr
                        || (sessionHnr.isUnbekannt()
                        && null != request.getParameter(SessionKey.HOERERNUMMER))) {
                    hoerernummerInSessionSetzen(request);
                } else {
                    LOGGER.trace("Hörernummer {} in HttpSession {} bereits gesetzt",
                            sessionHnr, session.getId());
                }
            } else {
                LOGGER.warn("Keine HttpSession");
            }
        }
        chain.doFilter(req, res);
    }

    private void logoutVerarbeiten(final HttpServletRequest request) {
        LOGGER.trace("Melde Hörer ab");
        final HttpSession session = request.getSession(false);
        /* TODO Richtig? */request.removeAttribute(SessionKey.HOERERNUMMER);
        /* TODO Richtig? */session.removeAttribute("scopedTarget.hoererSession");
        session.invalidate();
        LOGGER.debug("HttpSession {} invalidiert", session.getId());
    }

    private void hoerernummerInSessionSetzen(final HttpServletRequest request) {
        final String requestHnr = request.getParameter(SessionKey.HOERERNUMMER);
        final HttpSession session = request.getSession(false);
        final HoererSession scopedHoererSession =
                (HoererSession) session.getAttribute("scopedTarget.hoererSession");
        if (null == scopedHoererSession) {
            return;
        }
        if (isNotNullAndNotEmpty(requestHnr)) {
            final Hoerernummer hoerernummer = new Hoerernummer(requestHnr);
            session.setAttribute(SessionKey.HOERERNUMMER, hoerernummer);
            scopedHoererSession.hoererSetzen(hoerernummer);
            LOGGER.debug("Hörernummer {} für HttpSession {} gesetzt", requestHnr, session.getId());
        } else {
            session.setAttribute(SessionKey.HOERERNUMMER, Hoerernummer.UNBEKANNT);
            scopedHoererSession.hoererSetzen(Hoerernummer.UNBEKANNT);
            LOGGER.debug("Unbekannter Hörer für HttpSession {} gesetzt", session.getId());
        }
    }

    private boolean isNotNullAndNotEmpty(final String str) {
        return null != str && !str.isBlank();
    }

}

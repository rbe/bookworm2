/*
 * Copyright (C) 2011-2018 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchkatalog.ui;

import wbh.bookworm.hoerbuchkatalog.domain.hoerer.Hoerernummer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@WebFilter(servletNames = "FacesServlet")
public class HoerernummerFilter implements Filter {

    private static final Logger LOGGER = LoggerFactory.getLogger(HoerernummerFilter.class);

    private static final String HNR_KEY = "hnr";

    @Override
    public void init(final FilterConfig filterConfig) {
    }

    @Override
    public void doFilter(final ServletRequest req, final ServletResponse res, final FilterChain chain) throws IOException, ServletException {
        LOGGER.trace("Suche Hoerernummer im HttpRequest");
        final HttpServletRequest request = (HttpServletRequest) req;
        final HttpSession session = request.getSession(true);
        if (null != session) {
            final Hoerernummer sessionHnr = (Hoerernummer) session.getAttribute(HNR_KEY);
            if (request.getRequestURI().endsWith("logout")) {
                session.invalidate();
                LOGGER.debug("HttpSession {} invalidiert", session.getId());
            } else if (null == sessionHnr) {
                final String requestHnr = request.getParameter(HNR_KEY);
                if (null != requestHnr) {
                    session.setAttribute(HNR_KEY, new Hoerernummer(requestHnr));
                    LOGGER.debug("Hörernummer {} für HttpSession {} gesetzt", requestHnr, session.getId());
                } else {
                    session.setAttribute(HNR_KEY, Hoerernummer.UNBEKANNT);
                    LOGGER.debug("Unbekannter Hörer für HttpSession {} gesetzt", session.getId());
                }
            } else {
                LOGGER.trace("Hörernummer {} in HttpSession {} bereits gesetzt", sessionHnr, session.getId());
            }
        } else {
            LOGGER.warn("Keine HttpSession");
        }
        chain.doFilter(req, res);
    }

    @Override
    public void destroy() {
    }

}

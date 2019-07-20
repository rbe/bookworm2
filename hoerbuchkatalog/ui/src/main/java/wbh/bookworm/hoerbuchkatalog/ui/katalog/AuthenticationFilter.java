/*
 * Copyright (C) 2011-2018 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchkatalog.ui.katalog;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.function.Consumer;

@WebFilter(servletNames = "FacesServlet", urlPatterns = {"*.xhtml", "logout"})
@Component
public class AuthenticationFilter implements Filter {

    private static final Logger LOGGER = LoggerFactory.getLogger(AuthenticationFilter.class);

    private final Consumer<HttpServletRequest> hoerernummerHttpRequest;

    @Autowired
    public AuthenticationFilter(final Consumer<HttpServletRequest> hoerernummerHttpRequest) {
        this.hoerernummerHttpRequest = hoerernummerHttpRequest;
    }

    @Override
    public void doFilter(final ServletRequest req, final ServletResponse res,
                         final FilterChain chain) throws IOException, ServletException {
        final HttpServletRequest request = (HttpServletRequest) req;
        final String requestURI = request.getRequestURI();
        if (requestURI.endsWith("logout")) {
            logoutVerarbeiten(request);
            ((HttpServletResponse) res).sendRedirect(
                    "https://www.wbh-online.de" + request.getContextPath() + "/");
        } else {
            hoerernummerHttpRequest.accept(request);
            chain.doFilter(req, res);
        }
    }

    private void logoutVerarbeiten(final HttpServletRequest request) {
        LOGGER.debug("Melde Hörer ab (URI {})", request.getRequestURI());
        final HttpSession session = request.getSession(false);
        if (null != session) {
            session.removeAttribute(SessionKey.SCOPEDTARGET_HOERERSESSION);
            session.invalidate();
            LOGGER.debug("HttpSession {} invalidiert", session.getId());
        }
    }

}

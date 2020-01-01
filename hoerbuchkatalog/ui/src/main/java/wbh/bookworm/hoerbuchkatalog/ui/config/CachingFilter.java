/*
 * Copyright (C) 2011-2018 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchkatalog.ui.config;

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
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.ZonedDateTime;

@WebFilter(servletNames = "FacesServlet")
public class CachingFilter implements Filter {

    private static final Logger LOGGER = LoggerFactory.getLogger(CachingFilter.class);

    @Override
    public void init(final FilterConfig filterConfig) {
        // do nothing
    }

    @Override
    public void doFilter(final ServletRequest req, final ServletResponse res,
                         final FilterChain chain) throws IOException, ServletException {
        final HttpServletRequest request = (HttpServletRequest) req;
        final HttpServletResponse response = (HttpServletResponse) res;
        final String uri = request.getRequestURI();
        if (uri.endsWith(".xhtml")) {
            cache1Minute(request, response);
        }
        chain.doFilter(req, res);
    }

    private void cache1Minute(final HttpServletRequest request, final HttpServletResponse response) {
        LOGGER.trace("Setting HTTP header Cache-Control, Pragma, Expires to 1 Minute in request '{}'",
                request.getRequestURI());
        response.setHeader("Cache-Control", "private, no-cache, no-store, max-age=60, must-revalidate"); // HTTP 1.1.
        response.setHeader("Pragma", "no-cache"); // HTTP 1.0
        response.setDateHeader("Expires", ZonedDateTime.now().plusSeconds(60).toEpochSecond()); // Proxies
    }

    @SuppressWarnings("squid:UnusedPrivateMethod")
    private void noCache(final HttpServletRequest request, final HttpServletResponse response) {
        LOGGER.trace("Setting HTTP header Cache-Control, Pragma, Expires to no-cache in request '{}'",
                request.getRequestURI());
        response.setHeader("Cache-Control", "private, no-cache, no-store, max-age=0, must-revalidate"); // HTTP 1.1.
        response.setHeader("Pragma", "no-cache"); // HTTP 1.0
        response.setDateHeader("Expires", 0); // Proxies
    }

    @Override
    public void destroy() {
        // do nothing
    }

}

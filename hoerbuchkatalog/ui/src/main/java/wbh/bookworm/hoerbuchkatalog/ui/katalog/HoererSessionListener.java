/*
 * Copyright (C) 2011-2018 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchkatalog.ui.katalog;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.annotation.WebListener;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

@WebListener
public class HoererSessionListener implements HttpSessionListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(HoererSessionListener.class);

    @Override
    public void sessionCreated(final HttpSessionEvent se) {
        LOGGER.debug("HttpSession {} erstellt", se.getSession().getId());
    }

    @Override
    public void sessionDestroyed(final HttpSessionEvent se) {
        LOGGER.debug("HttpSession {} beendet", se.getSession().getId());
    }

}

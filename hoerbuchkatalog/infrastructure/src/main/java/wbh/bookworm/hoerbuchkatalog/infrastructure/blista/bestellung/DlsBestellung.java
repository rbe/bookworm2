/*
 * Copyright (C) 2011-2018 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchkatalog.infrastructure.blista.bestellung;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DlsBestellung {

    private static final Logger LOGGER = LoggerFactory.getLogger(DlsBestellung.class);

    private final BilletSender billetSender;

    @Autowired
    DlsBestellung(BilletSender billetSender) {
        this.billetSender = billetSender;
    }

    Quittung bestellen(final String userId, final String aghNummer) {
        MDC.put("userId", userId);
        MDC.put("aghNummer", aghNummer);
        Quittung quittung = null;
        try {
            final String abrufkennwort = billetSender.sendToServer(userId, aghNummer);
            final BilletStatus billetStatus = billetSender.billetStatus(userId, aghNummer);
            quittung = new Quittung(abrufkennwort, billetStatus.name());
        } catch (Exception e) {
            LOGGER.error("Konnte Bestellung nicht aufgeben", e);
        } finally {
            MDC.clear();
        }
        return quittung;
    }

    BilletStatus status(final String userId, final String aghNummer) {
        MDC.put("userId", userId);
        MDC.put("aghNummer", aghNummer);
        try {
            return billetSender.billetStatus(userId, aghNummer);
        } finally {
            MDC.clear();
        }
    }

}

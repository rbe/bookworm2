/*
 * Copyright (C) 2011-2018 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchkatalog.app.katalog;

import wbh.bookworm.hoerbuchkatalog.domain.hoerer.Hoerernummer;
import wbh.bookworm.hoerbuchkatalog.domain.katalog.Hoerbuch;
import wbh.bookworm.hoerbuchkatalog.domain.katalog.Suchergebnis;
import wbh.bookworm.hoerbuchkatalog.domain.katalog.Suchparameter;
import wbh.bookworm.hoerbuchkatalog.domain.katalog.Titelnummer;
import wbh.bookworm.hoerbuchkatalog.repository.katalog.Hoerbuchkatalog;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class HoerbuchkatalogService {

    private static final Logger LOGGER = LoggerFactory.getLogger(HoerbuchkatalogService.class);

    private final Hoerbuchkatalog hoerbuchkatalog;

    @Autowired
    public HoerbuchkatalogService(final Hoerbuchkatalog hoerbuchkatalog) {
        this.hoerbuchkatalog = hoerbuchkatalog;
    }

    public Suchergebnis sucheNachStichwort(final Hoerernummer hoerernummer, final String stichwort) {
        LOGGER.info("Suche {}", stichwort);
        return hoerbuchkatalog.sucheNachStichwort(stichwort);
    }

    public Suchergebnis suchen(final Hoerernummer hoerernummer, final Suchparameter suchparameter) {
        LOGGER.info("Suche {}", suchparameter);
        return hoerbuchkatalog.suchen(suchparameter);
    }

    public Hoerbuch finde(final Hoerernummer hoerernummer, final Titelnummer titelnummer) {
        return hoerbuchkatalog.finde(titelnummer);
    }

}

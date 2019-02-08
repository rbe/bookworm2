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

import java.util.Arrays;
import java.util.List;

@Service
public final class HoerbuchkatalogService {

    private static final Logger LOGGER = LoggerFactory.getLogger(HoerbuchkatalogService.class);

    private final Hoerbuchkatalog hoerbuchkatalog;

    @Autowired
    public HoerbuchkatalogService(final Hoerbuchkatalog hoerbuchkatalog) {
        this.hoerbuchkatalog = hoerbuchkatalog;
    }

    public Suchergebnis sucheNachStichwort(final Hoerernummer hoerernummer, final String stichwort) {
        LOGGER.trace("Hörer {} Stichwort {}", hoerernummer, stichwort);
        final Suchergebnis suchergebnis = hoerbuchkatalog.sucheNachStichwort(stichwort);
        LOGGER.info("Hörer {}: Suche nach Stichwort '{}' ergab {} Treffer",
                hoerernummer, stichwort, suchergebnis.getAnzahl());
        return suchergebnis;
    }

    public Suchergebnis suchen(final Hoerernummer hoerernummer, final Suchparameter suchparameter) {
        LOGGER.trace("Hörer {} Suchparameter {}", hoerernummer, suchparameter);
        final Suchergebnis suchergebnis = hoerbuchkatalog.suchen(suchparameter);
        LOGGER.info("Hörer {}: Suche nach '{}' ergab {} Treffer",
                hoerernummer, suchparameter, suchergebnis.getAnzahl());
        return suchergebnis;
    }

    public boolean hoerbuchVorhanden(final Hoerernummer hoerernummer, final Titelnummer titelnummer) {
        LOGGER.trace("Hörer {} Titelnummer {}", hoerernummer, titelnummer);
        return hoerbuchkatalog.enthaelt(titelnummer);
    }

    public boolean hoerbuchDownloadbar(final Hoerernummer hoerernummer, final Titelnummer titelnummer) {
        LOGGER.trace("Hörer {} Titelnummer {}", hoerernummer, titelnummer);
        return hoerbuchkatalog.hoerbuchDownloadbar(titelnummer);
    }

    public Hoerbuch hole(final Hoerernummer hoerernummer, final Titelnummer titelnummer) {
        LOGGER.trace("Hörer {} Titelnummer {}", hoerernummer, titelnummer);
        return hoerbuchkatalog.hole(titelnummer);
    }

    public List<Hoerbuch> hole(final Hoerernummer hoerernummer, final Titelnummer... titelnummern) {
        LOGGER.trace("Hörer {} Titelnummer {}", hoerernummer, Arrays.asList(titelnummern));
        return hoerbuchkatalog.hole(titelnummern);
    }

}

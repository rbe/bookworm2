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
import wbh.bookworm.hoerbuchkatalog.repository.config.RepositoryResolver;
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

    private final RepositoryResolver repositoryResolver;

    @Autowired
    public HoerbuchkatalogService(final RepositoryResolver repositoryResolver) {
        this.repositoryResolver = repositoryResolver;
    }

    public Suchergebnis suchen(final Hoerernummer hoerernummer, final Suchparameter suchparameter) {
        final Hoerbuchkatalog hoerbuchkatalog = repositoryResolver.hoerbuchkatalog();
        LOGGER.trace("Hörer {} Suchparameter {} in {}", hoerernummer, suchparameter, hoerbuchkatalog);
        final Suchergebnis suchergebnis = hoerbuchkatalog.suchen(suchparameter);
        LOGGER.info("Hörer {} Suche nach '{}' ergab {} Treffer",
                hoerernummer, suchparameter, suchergebnis.getAnzahl());
        return suchergebnis;
    }

    public boolean hoerbuchVorhanden(final Hoerernummer hoerernummer, final Titelnummer titelnummer) {
        LOGGER.trace("Hörer {} Titelnummer {}", hoerernummer, titelnummer);
        final Hoerbuchkatalog hoerbuchkatalog = repositoryResolver.hoerbuchkatalog();
        return hoerbuchkatalog.enthaelt(titelnummer);
    }

    public boolean hoerbuchDownloadbar(final Hoerernummer hoerernummer, final Titelnummer titelnummer) {
        LOGGER.trace("Hörer {} Titelnummer {}", hoerernummer, titelnummer);
        final Hoerbuchkatalog hoerbuchkatalog = repositoryResolver.hoerbuchkatalog();
        return hoerbuchkatalog.hoerbuchDownloadbar(titelnummer);
    }

    public Hoerbuch hole(final Hoerernummer hoerernummer, final Titelnummer titelnummer) {
        LOGGER.trace("Hörer {} Titelnummer {}", hoerernummer, titelnummer);
        final Hoerbuchkatalog hoerbuchkatalog = repositoryResolver.hoerbuchkatalog();
        return hoerbuchkatalog.hole(titelnummer);
    }

    public List<Hoerbuch> hole(final Hoerernummer hoerernummer, final Titelnummer... titelnummern) {
        LOGGER.trace("Hörer {} Titelnummer {}", hoerernummer, Arrays.asList(titelnummern));
        final Hoerbuchkatalog hoerbuchkatalog = repositoryResolver.hoerbuchkatalog();
        return hoerbuchkatalog.hole(titelnummern);
    }

}

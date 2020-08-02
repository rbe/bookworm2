/*
 * Copyright (C) 2011-2019 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchkatalog.app.lieferung;

import java.util.Collections;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import wbh.bookworm.hoerbuchkatalog.domain.lieferung.HoererBlistaDownloads;
import wbh.bookworm.hoerbuchkatalog.repository.lieferung.DownloadsRepository;
import wbh.bookworm.shared.domain.mandant.Hoerernummer;

@Service
public final class DownloadsLieferungService {

    private static final Logger LOGGER = LoggerFactory.getLogger(DownloadsLieferungService.class);

    private final DownloadsRepository downloadsRepository;

    @Autowired
    public DownloadsLieferungService(final DownloadsRepository downloadsRepository) {
        LOGGER.trace("Initializing");
        this.downloadsRepository = downloadsRepository;
    }

    public HoererBlistaDownloads lieferungen(final Hoerernummer hoerernummer) {
        /* TODO Security Context */if (hoerernummer.isUnbekannt()) {
            return new HoererBlistaDownloads(hoerernummer, Collections.emptyList());
        } else {
            LOGGER.trace("Hole Downloads für Hörer {}", hoerernummer);
            final HoererBlistaDownloads lieferungen = downloadsRepository.lieferungen(hoerernummer);
            if (!lieferungen.hatFehler()) {
                LOGGER.info("{} Download(s) für Hörer {} geholt, davon {} bezugsfähig",
                        lieferungen.alle().size(), hoerernummer, lieferungen.bezuegsfaehige().size());
            } else {
                LOGGER.warn("Downloads für Hörer {} konnten nicht abgerufen werden: {} {}",
                        hoerernummer, lieferungen.getFehlercode(), lieferungen.getFehlermeldung());
            }
            return lieferungen;
        }
    }

    public long anzahlLieferungenInDiesemMonat(final Hoerernummer hoerernummer) {
        return downloadsRepository.anzahlLieferungenInDiesemMonat(hoerernummer);
    }

    public boolean isMaxDownloadsProMonatErreicht(final Hoerernummer hoerernummer) {
        return anzahlLieferungenInDiesemMonat(hoerernummer) >= 10;
    }

    public long anzahlLieferungenHeute(final Hoerernummer hoerernummer) {
        return downloadsRepository.anzahlLieferungenHeute(hoerernummer);
    }

    public boolean isMaxDownloadsProTagErreicht(final Hoerernummer hoerernummer) {
        return anzahlLieferungenHeute(hoerernummer) >= 5;
    }

}

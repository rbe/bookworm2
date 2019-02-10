/*
 * Copyright (C) 2011-2019 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchkatalog.app.hoerer;

import wbh.bookworm.hoerbuchkatalog.domain.hoerer.Hoerer;
import wbh.bookworm.hoerbuchkatalog.domain.hoerer.Hoerernummer;
import wbh.bookworm.hoerbuchkatalog.repository.nutzerdaten.HoererRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public final class HoererService {

    private static final Logger LOGGER = LoggerFactory.getLogger(HoererService.class);

    private final HoererRepository hoererRepository;

    @Autowired
    public HoererService(final HoererRepository hoererRepository) {
        this.hoererRepository = hoererRepository;
    }

    public Optional<Hoerer> hoerer(final Hoerernummer hoerernummer) {
        /* TODO Security Context */if (hoerernummer.isUnbekannt()) {
            return Optional.empty();// TODO Hoerer.UNBEKANNT;
        } else {
            final Hoerer hoerer = hoererRepository.hoerer(hoerernummer);
            if (hoerer.isBekannt()) {
                LOGGER.debug("Hörerdaten für Hörer {} gefunden", hoerernummer);
                return Optional.of(hoerer);
            } else {
                LOGGER.warn("Keine Hörerdaten für Hörer {} gefunden", hoerernummer);
            }
        }
        return Optional.empty();
    }

}

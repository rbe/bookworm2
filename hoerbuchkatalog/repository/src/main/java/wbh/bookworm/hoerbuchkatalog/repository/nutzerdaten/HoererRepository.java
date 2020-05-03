/*
 * Copyright (C) 2011-2019 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchkatalog.repository.nutzerdaten;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;

import wbh.bookworm.hoerbuchkatalog.domain.hoerer.Hoerer;
import wbh.bookworm.hoerbuchkatalog.domain.lieferung.Belastung;
import wbh.bookworm.shared.domain.Hoerernummer;

import aoc.mikrokosmos.ddd.repository.DomainRepositoryComponent;

@Configuration
@DomainRepositoryComponent
public class HoererRepository {

    private static final Logger LOGGER = LoggerFactory.getLogger(HoererRepository.class);

    private final ApplicationContext applicationContext;

    private AtomicReference<HoererMapper> aktuellerHoererMapper;

    @Autowired
    public HoererRepository(final ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
        this.aktuellerHoererMapper = new AtomicReference<>();
        datenEinlesen();
    }

    synchronized void datenEinlesen() {
        LOGGER.info("Lese Hörerdaten ein");
        aktuellerHoererMapper.set(applicationContext.getBean(HoererMapper.class));
        LOGGER.info("Hörerdaten eingelesen");
    }

    public Optional<Hoerer> hoerer(final Hoerernummer hoerernummer) {
        return Optional.ofNullable(aktuellerHoererMapper.get().hoerer(hoerernummer));
    }

    public List<Belastung> belastungen(final Hoerernummer hoerernummer) {
        return aktuellerHoererMapper.get().belastungenFuer(hoerernummer);
    }

}

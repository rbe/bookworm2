/*
 * Copyright (C) 2011-2019 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchkatalog.repository.lieferung;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import wbh.bookworm.hoerbuchkatalog.domain.lieferung.Bestellkarte;
import wbh.bookworm.hoerbuchkatalog.domain.lieferung.CdLieferungAktualisiert;
import wbh.bookworm.hoerbuchkatalog.domain.lieferung.ErledigteBestellkarte;
import wbh.bookworm.shared.domain.hoerer.Hoerernummer;

import aoc.mikrokosmos.ddd.event.DomainEventPublisher;
import aoc.mikrokosmos.ddd.repository.DomainRepositoryComponent;

// TODO Daten aus dem Mapper hier speichern
// TODO Umbenennen: (Hoerbuch)ArchivRepository
@DomainRepositoryComponent
public class CdLieferungRepository {

    private static final Logger LOGGER = LoggerFactory.getLogger(CdLieferungRepository.class);

    private final ApplicationContext applicationContext;

    private final AtomicReference<BestellkartenMapper> aktuellerBestellkartenMapper;

    private final AtomicReference<ErledigteBestellkartenMapper> aktuellerErledigteBestellkartenMapper;

    @Autowired
    public CdLieferungRepository(final ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
        this.aktuellerBestellkartenMapper = new AtomicReference<>();
        this.aktuellerErledigteBestellkartenMapper = new AtomicReference<>();
        datenEinlesen();
    }

    synchronized void datenEinlesen() {
        LOGGER.info("Lese erledigte Bestellkarten und Bestellkarten ein");
        aktuellerBestellkartenMapper.set(applicationContext.getBean(BestellkartenMapper.class));
        aktuellerErledigteBestellkartenMapper.set(applicationContext.getBean(
                ErledigteBestellkartenMapper.class));
        DomainEventPublisher.global().publishAsync(new CdLieferungAktualisiert());
        LOGGER.info("Erledigte Bestellkarten und Bestellkarten eingelesen");
    }

    public boolean hatBestellkarten() {
        return aktuellerBestellkartenMapper.get().hatDatenEingelesen();
    }

    public List<Bestellkarte> bestellkarten(final Hoerernummer hoerernummer) {
        return aktuellerBestellkartenMapper.get().bestellkartenFuer(hoerernummer);
    }

    public boolean hatErledigteBestellkarten() {
        return aktuellerErledigteBestellkartenMapper.get().hatDatenEingelesen();
    }

    public List<ErledigteBestellkarte> erledigteBestellkarten(final Hoerernummer hoerernummer) {
        return aktuellerErledigteBestellkartenMapper.get().erledigteBestellkartenFuer(hoerernummer);
    }

}

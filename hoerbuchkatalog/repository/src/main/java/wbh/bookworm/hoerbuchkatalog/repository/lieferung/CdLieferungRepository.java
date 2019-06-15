/*
 * Copyright (C) 2011-2019 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchkatalog.repository.lieferung;

import wbh.bookworm.hoerbuchkatalog.domain.hoerer.Hoerernummer;
import wbh.bookworm.hoerbuchkatalog.domain.lieferung.Bestellkarte;
import wbh.bookworm.hoerbuchkatalog.domain.lieferung.ErledigteBestellkarte;

import aoc.ddd.repository.DomainRepositoryComponent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

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
        LOGGER.info("Erledigte Bestellkarten und Bestellkarten eingelesen");
    }

    private class BestellkartenCallable implements Callable<Void> {

        @Override
        public Void call() throws Exception {
            bestellkartenMapper.leseAs400Datei(
                    StandardCharsets.ISO_8859_1, 9_000,
                    cdLieferungRepositoryConfig.getDirectory().resolve("bkstp.csv"));
            return null;
        }

    }

    public List<Bestellkarte> bestellkarten(final Hoerernummer hoerernummer) {
        return aktuellerBestellkartenMapper.get().bestellkartenFuer(hoerernummer);
    }

    public List<ErledigteBestellkarte> erledigteBestellkarten(final Hoerernummer hoerernummer) {
        return aktuellerErledigteBestellkartenMapper.get().erledigteBestellkartenFuer(hoerernummer);
    }

    public boolean hatErledigteBestellkarten() {
        return aktuellerErledigteBestellkartenMapper.get().hatDatenEingelesen();
    }

}

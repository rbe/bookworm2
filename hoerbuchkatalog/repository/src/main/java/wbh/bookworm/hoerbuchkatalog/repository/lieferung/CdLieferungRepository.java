/*
 * Copyright (C) 2011-2019 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchkatalog.repository.lieferung;

import wbh.bookworm.hoerbuchkatalog.domain.hoerer.Hoerernummer;
import wbh.bookworm.hoerbuchkatalog.domain.lieferung.Bestellkarte;
import wbh.bookworm.hoerbuchkatalog.domain.lieferung.ErledigteBestellkarte;

import aoc.ddd.repository.DomainRespositoryComponent;
import aoc.tools.datatransfer.Executor;

import org.springframework.beans.factory.annotation.Autowired;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;

// TODO Daten aus dem Mapper hier speichern
@DomainRespositoryComponent
public class CdLieferungRepository {

    private final CdLieferungRepositoryConfig cdLieferungRepositoryConfig;

    private final BestellkartenMapper bestellkartenMapper;

    private final ErledigteBestellkartenMapper erledigteBestellkartenMapper;

    @Autowired
    public CdLieferungRepository(final ExecutorService executorService,
                                 final CdLieferungRepositoryConfig cdLieferungRepositoryConfig,
                                 final BestellkartenMapper bestellkartenMapper,
                                 final ErledigteBestellkartenMapper erledigteBestellkartenMapper) {
        this.cdLieferungRepositoryConfig = cdLieferungRepositoryConfig;
        this.bestellkartenMapper = bestellkartenMapper;
        this.erledigteBestellkartenMapper = erledigteBestellkartenMapper;
        Executor.invokeAllAndGet(executorService, Arrays.asList(
                new BestellkartenCallable(), new ErledigteBestellkartenCallable()));
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
        return bestellkartenMapper.bestellkartenFuer(hoerernummer);
    }

    private class ErledigteBestellkartenCallable implements Callable<Void> {

        @Override
        public Void call() throws Exception {
            erledigteBestellkartenMapper.leseAs400Datei(
                    StandardCharsets.ISO_8859_1, 2_900_000,
                    cdLieferungRepositoryConfig.getDirectory().resolve("bkrxstp.csv"));
            return null;
        }

    }

    public List<ErledigteBestellkarte> erledigteBestellkarten(final Hoerernummer hoerernummer) {
        return erledigteBestellkartenMapper.erledigteBestellkartenFuer(hoerernummer);
    }

}

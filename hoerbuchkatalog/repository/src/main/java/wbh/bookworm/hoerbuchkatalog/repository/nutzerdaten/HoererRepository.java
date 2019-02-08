/*
 * Copyright (C) 2011-2019 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchkatalog.repository.nutzerdaten;

import wbh.bookworm.hoerbuchkatalog.domain.hoerer.Hoerer;
import wbh.bookworm.hoerbuchkatalog.domain.hoerer.Hoerernummer;
import wbh.bookworm.hoerbuchkatalog.domain.lieferung.Belastung;

import aoc.ddd.repository.DomainRespositoryComponent;
import aoc.tools.datatransfer.Executor;

import org.springframework.beans.factory.annotation.Autowired;

import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;

@DomainRespositoryComponent
public class HoererRepository {

    private final HoererRepositoryConfig hoererRepositoryConfig;

    private final HoererMapper hoererMapper;

    @Autowired
    public HoererRepository(final ExecutorService executorService,
                            final HoererRepositoryConfig hoererRepositoryConfig,
                            final HoererMapper hoererMapper) {
        this.hoererRepositoryConfig = hoererRepositoryConfig;
        this.hoererMapper = hoererMapper;
        Executor.invokeAllAndGet(executorService,
                Collections.singletonList(new HoererMapperCallable()));
    }

    private class HoererMapperCallable implements Callable<Void> {

        @Override
        public Void call() throws Exception {
            hoererMapper.leseAs400Dateien(
                    StandardCharsets.ISO_8859_1, 9_000,
                    hoererRepositoryConfig.getDirectory().resolve("hoerstp.csv"),
                    hoererRepositoryConfig.getDirectory().resolve("hoekzstp.csv"),
                    hoererRepositoryConfig.getDirectory().resolve("hoebstp.csv"));
            return null;
        }

    }

    public Hoerer hoerer(final Hoerernummer hoerernummer) {
        return hoererMapper.hoerer(hoerernummer);
    }

    public List<Belastung> belastungen(final Hoerernummer hoerernummer) {
        return hoererMapper.belastungenFuer(hoerernummer);
    }

}

/*
 * Copyright (C) 2011-2019 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchkatalog.repository.nutzerdaten;

import wbh.bookworm.hoerbuchkatalog.domain.hoerer.Hoerer;
import wbh.bookworm.hoerbuchkatalog.domain.hoerer.Hoerernummer;
import wbh.bookworm.hoerbuchkatalog.domain.lieferung.Belastung;
import wbh.bookworm.hoerbuchkatalog.domain.lieferung.Bestellkarte;

import aoc.ddd.repository.DomainRespositoryComponent;

import org.springframework.beans.factory.annotation.Autowired;

import java.nio.charset.StandardCharsets;
import java.util.List;

@DomainRespositoryComponent
public class HoererRepository {

    private final HoererRepositoryConfig hoererRepositoryConfig;

    private final HoererMapper hoererMapper;

    private final BestellkartenMapper bestellkartenMapper;

    private final ErledigteBestellkartenMapper erledigteBestellkartenMapper;

    @Autowired
    public HoererRepository(final HoererRepositoryConfig hoererRepositoryConfig,
                            final HoererMapper hoererMapper,
                            final BestellkartenMapper bestellkartenMapper,
                            final ErledigteBestellkartenMapper erledigteBestellkartenMapper) {
        this.hoererRepositoryConfig = hoererRepositoryConfig;
        this.hoererMapper = hoererMapper;
        initialisiereHoerer();
        this.bestellkartenMapper = bestellkartenMapper;
        initialisiereBestellkarten();
        this.erledigteBestellkartenMapper = erledigteBestellkartenMapper;
        initialisiereErledigteBestellkarten();
    }

    private void initialisiereHoerer() {
        hoererMapper.leseAs400Dateien(
                StandardCharsets.ISO_8859_1, 9_000,
                hoererRepositoryConfig.getDirectory().resolve("hoerstp.csv"),
                hoererRepositoryConfig.getDirectory().resolve("hoekzstp.csv"),
                hoererRepositoryConfig.getDirectory().resolve("hoebstp.csv"));
    }

    public Hoerer hoerer(final Hoerernummer hoerernummer) {
        return hoererMapper.hoerer(hoerernummer);
    }

    public List<Belastung> belastungen(final Hoerernummer hoerernummer) {
        return hoererMapper.belastungenFuer(hoerernummer);
    }

    private void initialisiereBestellkarten() {
        bestellkartenMapper.leseAs400Datei(
                hoererRepositoryConfig.getDirectory().resolve("bkstp.csv"),
                StandardCharsets.ISO_8859_1, 9_000);
    }

    public List<Bestellkarte> bestellkarten(final Hoerernummer hoerernummer) {
        return bestellkartenMapper.bestellkartenFuer(hoerernummer);
    }

    private void initialisiereErledigteBestellkarten() {
        erledigteBestellkartenMapper.leseAs400Datei(
                StandardCharsets.ISO_8859_1, 2_900_000,
                hoererRepositoryConfig.getDirectory().resolve("bkrxstp.csv"));
    }

}

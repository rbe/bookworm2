/*
 * Copyright (C) 2011-2020 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchdienst.adapter.required.mandantrepository.impl;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import wbh.bookworm.hoerbuchdienst.domain.required.mandantrepository.Mandant;
import wbh.bookworm.hoerbuchdienst.domain.required.mandantrepository.MandantRepository;
import wbh.bookworm.shared.domain.Hoerernummer;
import wbh.bookworm.shared.domain.MandantenId;

@Singleton
final class MandantRepositoryImpl implements MandantRepository {

    private static final Logger LOGGER = LoggerFactory.getLogger(MandantRepositoryImpl.class);

    private final Map<MandantenId, Mandant> mandanten;

    private final ObjectMapper objectMapper;

    private final Lock objectMapperLock;

    @Inject
    MandantRepositoryImpl() {
        mandanten = new ConcurrentHashMap<>(3);
        makeMandant("WBH", "06", Collections.emptySet());
        objectMapper = new ObjectMapper();
        objectMapperLock = new ReentrantLock();
    }

    private void makeMandant(final String name, final String id, final Set<Hoerernummer> hoerernummern) {
        final MandantenId mandantenId = new MandantenId(id);
        mandanten.put(mandantenId, new Mandant(mandantenId, hoerernummern));
    }

    @Override
    public boolean existiert(final MandantenId mandantenId) {
        return find(mandantenId).isPresent();
    }

    @Override
    public Optional<Mandant> find(final MandantenId mandantenId) {
        if (!mandanten.containsKey(mandantenId)) {
            read(mandantenId);
        }
        return mandanten.containsKey(mandantenId)
                ? Optional.of(new Mandant(mandanten.get(mandantenId)))
                : Optional.empty();
    }

    private void read(final MandantenId mandantenId) {
        final File jsonFile = Path.of(mandantenId + ".json").toFile();
        objectMapperLock.lock();
        try {
            final Mandant fromFile = objectMapper.readValue(jsonFile, Mandant.class);
            mandanten.put(mandantenId, fromFile);
        } catch (IOException e) {
            LOGGER.error("", e);
        } finally {
            objectMapperLock.unlock();
        }
    }

    @Override
    public boolean hoerernummerExistiert(final MandantenId mandantenId, final Hoerernummer hoerernummer) {
        return mandanten.get(mandantenId).contains(hoerernummer);
    }

    @Override
    public void fuegeHoererHinzu(final MandantenId mandantenId, final Hoerernummer hoerernummer) {
        final Mandant mandant = mandanten.get(mandantenId);
        mandant.add(hoerernummer);
        write(mandant);
    }

    private void write(final Mandant mandant) {
        final File jsonFile = Path.of(mandant.getId() + ".json").toFile();
        objectMapperLock.lock();
        try {
            objectMapper.writeValue(jsonFile, mandant);
        } catch (IOException e) {
            LOGGER.error("", e);
        } finally {
            objectMapperLock.unlock();
        }
    }

    @Override
    public void fuegeHoererHinzu(final MandantenId mandantenId, final Set<Hoerernummer> hoerernummer) {
        final Mandant mandant = mandanten.get(mandantenId);
        mandant.add(hoerernummer);
        write(mandant);
    }

}

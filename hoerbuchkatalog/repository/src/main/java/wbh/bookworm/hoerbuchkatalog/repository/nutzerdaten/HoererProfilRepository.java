/*
 * Copyright (C) 2011-2019 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchkatalog.repository.nutzerdaten;

import java.nio.file.Path;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import wbh.bookworm.hoerbuchkatalog.domain.hoerer.HoererProfil;
import wbh.bookworm.shared.domain.Hoerernummer;

import aoc.mikrokosmos.ddd.repository.DomainRepositoryComponent;
import aoc.mikrokosmos.ddd.repository.JsonDomainRepository;

// TODO @DomainRepositoryComponent
public class HoererProfilRepository extends JsonDomainRepository<HoererProfil, Hoerernummer> {

    private static final Logger LOGGER = LoggerFactory.getLogger(HoererProfilRepository.class);

    public HoererProfilRepository(final Path storagePath) {
        super(HoererProfil.class, Hoerernummer.class, storagePath);
    }

}

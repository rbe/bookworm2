/*
 * Copyright (C) 2011-2018 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchkatalog.repository.bestellung;

import java.nio.file.Path;

import wbh.bookworm.hoerbuchkatalog.domain.bestellung.BestellungId;
import wbh.bookworm.hoerbuchkatalog.domain.bestellung.Session;

import aoc.mikrokosmos.ddd.repository.JsonDomainRepository;

// @Bean in BestellungAppConfig
public class SessionRepository extends JsonDomainRepository<Session, BestellungId> {

    public SessionRepository(final Path storagePath) {
        super(Session.class, BestellungId.class, storagePath);
    }

}

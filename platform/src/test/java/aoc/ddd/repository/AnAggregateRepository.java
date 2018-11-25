/*
 * Copyright (C) 2011-2018 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package aoc.ddd.repository;

import java.nio.file.Path;

class AnAggregateRepository extends JsonDomainRepository<AnAggregate, AnAggregateId> {

    AnAggregateRepository(final Path storagePath) {
        super(AnAggregate.class, AnAggregateId.class, storagePath);
    }

}

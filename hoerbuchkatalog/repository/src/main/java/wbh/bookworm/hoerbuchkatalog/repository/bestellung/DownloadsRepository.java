/*
 * Copyright (C) 2011-2018 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchkatalog.repository.bestellung;

import java.nio.file.Path;
import java.util.Optional;

import wbh.bookworm.hoerbuchkatalog.domain.bestellung.Downloads;
import wbh.bookworm.hoerbuchkatalog.domain.bestellung.DownloadsId;
import wbh.bookworm.shared.domain.Hoerernummer;

import aoc.mikrokosmos.ddd.repository.JsonDomainRepository;

// TODO @DomainRepositoryComponent
public class DownloadsRepository extends JsonDomainRepository<Downloads, DownloadsId> {

    public DownloadsRepository(final Path storagePath) {
        super(Downloads.class, DownloadsId.class, storagePath);
    }

    private DownloadsId downloadsIdIdFuerHoerer(final Hoerernummer hoerernummer) {
        return new DownloadsId(hoerernummer + "-Downloads");
    }

    public Downloads erstellen(final Hoerernummer hoerernummer) {
        return new Downloads(downloadsIdIdFuerHoerer(hoerernummer), hoerernummer);
    }

    public Optional<Downloads> load(final Hoerernummer hoerernummer) {
        return super.load(downloadsIdIdFuerHoerer(hoerernummer));
    }

}
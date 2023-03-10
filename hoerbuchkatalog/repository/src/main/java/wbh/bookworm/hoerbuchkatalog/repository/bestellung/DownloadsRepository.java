/*
 * Copyright (C) 2011-2018 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchkatalog.repository.bestellung;

import java.nio.file.Path;
import java.util.Collections;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import wbh.bookworm.hoerbuchkatalog.domain.bestellung.Downloads;
import wbh.bookworm.hoerbuchkatalog.domain.bestellung.DownloadsId;
import wbh.bookworm.shared.domain.Hoerernummer;

import aoc.mikrokosmos.ddd.repository.JsonDomainRepository;

// TODO @DomainRepositoryComponent
public class DownloadsRepository extends JsonDomainRepository<Downloads, DownloadsId> {

    private static final Logger LOGGER = LoggerFactory.getLogger(DownloadsRepository.class);

    public DownloadsRepository(final Path storagePath) {
        super(Downloads.class, DownloadsId.class, storagePath);
    }

    public Downloads erstellen(final Hoerernummer hoerernummer) {
        final DownloadsId unbekanntId = downloadsIdIdFuerHoerer(Hoerernummer.UNBEKANNT);
        if (hoerernummer.isUnbekannt()) {
            LOGGER.warn("Erstelle kein Downloads(Aggregate) für Hörer unbekannt");
            return fresh(unbekanntId);
        }
        final Downloads unbekannt = load(unbekanntId).orElse(fresh(unbekanntId));
        final DownloadsId downloadsId = downloadsIdIdFuerHoerer(hoerernummer);
        return new Downloads(downloadsId, hoerernummer,
                unbekannt.getAnzahlBestellungenProAusleihzeitraum(),
                unbekannt.getAnzahlBestellungenProTag(),
                unbekannt.getAnzahlDownloadsProHoerbuch(),
                Collections.emptyMap());
    }

    private Downloads fresh(final DownloadsId unbekanntId) {
        return new Downloads(unbekanntId, Hoerernummer.UNBEKANNT,
                30, 10, 5,
                Collections.emptyMap());
    }

    public Optional<Downloads> load(final Hoerernummer hoerernummer) {
        return super.load(downloadsIdIdFuerHoerer(hoerernummer));
    }

    private DownloadsId downloadsIdIdFuerHoerer(final Hoerernummer hoerernummer) {
        return new DownloadsId(hoerernummer + "-Downloads");
    }

}

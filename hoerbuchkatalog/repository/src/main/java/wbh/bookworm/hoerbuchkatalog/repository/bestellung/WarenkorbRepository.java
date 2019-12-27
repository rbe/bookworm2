/*
 * Copyright (C) 2011-2018 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchkatalog.repository.bestellung;

import wbh.bookworm.hoerbuchkatalog.domain.bestellung.CdAusDemWarenkorbEntfernt;
import wbh.bookworm.hoerbuchkatalog.domain.bestellung.CdInDenWarenkorbGelegt;
import wbh.bookworm.hoerbuchkatalog.domain.bestellung.CdWarenkorb;
import wbh.bookworm.hoerbuchkatalog.domain.bestellung.DownloadAusDemWarenkorbEntfernt;
import wbh.bookworm.hoerbuchkatalog.domain.bestellung.DownloadInDenWarenkorbGelegt;
import wbh.bookworm.hoerbuchkatalog.domain.bestellung.DownloadWarenkorb;
import wbh.bookworm.hoerbuchkatalog.domain.bestellung.Warenkorb;
import wbh.bookworm.hoerbuchkatalog.domain.bestellung.WarenkorbGeleert;
import wbh.bookworm.hoerbuchkatalog.domain.bestellung.WarenkorbId;
import wbh.bookworm.hoerbuchkatalog.domain.hoerer.Hoerernummer;

import aoc.mikrokosmos.ddd.repository.JsonDomainRepository;

import java.nio.file.Path;
import java.util.Optional;

// TODO @DomainRepositoryComponent
public class WarenkorbRepository extends JsonDomainRepository<Warenkorb, WarenkorbId> {

    /** Via @Bean erzeugen */
    public WarenkorbRepository(final Path storagePath) {
        super(Warenkorb.class, WarenkorbId.class, storagePath);
        saveOnEvent(logger, CdInDenWarenkorbGelegt.class);
        saveOnEvent(logger, CdAusDemWarenkorbEntfernt.class);
        saveOnEvent(logger, DownloadInDenWarenkorbGelegt.class);
        saveOnEvent(logger, DownloadAusDemWarenkorbEntfernt.class);
        deleteOnEvent(logger, WarenkorbGeleert.class);
    }

    public CdWarenkorb cdWarenkorbErstellen(final WarenkorbId warenkorbId, final Hoerernummer hoerernummer) {
        return new CdWarenkorb(warenkorbId, hoerernummer);
    }

    public Optional<CdWarenkorb> loadCdWarenkorb(final WarenkorbId warenkorbId) {
        return super.load(warenkorbId, CdWarenkorb.class);
    }

    public DownloadWarenkorb downloadWarenkorbErstellen(final WarenkorbId warenkorbId,
                                                        final Hoerernummer hoerernummer) {
        return new DownloadWarenkorb(warenkorbId, hoerernummer);
    }

    public Optional<DownloadWarenkorb> loadDownloadWarenkorb(final WarenkorbId warenkorbId) {
        return super.load(warenkorbId, DownloadWarenkorb.class);
    }

}

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
import wbh.bookworm.platform.ddd.repository.DomainRespositoryComponent;
import wbh.bookworm.platform.ddd.repository.JsonDomainRepository;

import java.nio.file.Path;
import java.util.Optional;

@DomainRespositoryComponent
public class WarenkorbRepository extends JsonDomainRepository<Warenkorb, WarenkorbId> {

    public WarenkorbRepository() {
        super(Warenkorb.class, WarenkorbId.class);
        saveOnEvent(logger, CdInDenWarenkorbGelegt.class);
        saveOnEvent(logger, CdAusDemWarenkorbEntfernt.class);
        saveOnEvent(logger, DownloadInDenWarenkorbGelegt.class);
        saveOnEvent(logger, DownloadAusDemWarenkorbEntfernt.class);
        saveOnEvent(logger, WarenkorbGeleert.class);
    }

    public WarenkorbRepository(final Path storagePath) {
        super(Warenkorb.class, WarenkorbId.class, storagePath);
    }

    private WarenkorbId cdWarenkorbIdFuerHoerer(final Hoerernummer hoerernummer) {
        return new WarenkorbId(hoerernummer + "-CD");
    }

    public CdWarenkorb cdWarenkorbErstellen(final Hoerernummer hoerernummer) {
        return new CdWarenkorb(cdWarenkorbIdFuerHoerer(hoerernummer), hoerernummer);
    }

    public Optional<CdWarenkorb> loadCdWarenkorb(final Hoerernummer hoerernummer) {
        return super.load(cdWarenkorbIdFuerHoerer(hoerernummer), CdWarenkorb.class);
    }

    private WarenkorbId downloadWarenkorbIdFuerHoerer(final Hoerernummer hoerernummer) {
        return new WarenkorbId(hoerernummer + "-Download");
    }

    public DownloadWarenkorb downloadWarenkorbErstellen(final Hoerernummer hoerernummer) {
        return new DownloadWarenkorb(downloadWarenkorbIdFuerHoerer(hoerernummer), hoerernummer);
    }

    public Optional<DownloadWarenkorb> loadDownloadWarenkorb(final Hoerernummer hoerernummer) {
        return super.load(downloadWarenkorbIdFuerHoerer(hoerernummer), DownloadWarenkorb.class);
    }

}

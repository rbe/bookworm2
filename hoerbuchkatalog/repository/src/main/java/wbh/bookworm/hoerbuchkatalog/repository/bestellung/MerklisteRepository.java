/*
 * Copyright (C) 2011-2018 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchkatalog.repository.bestellung;

import java.nio.file.Path;
import java.util.Optional;

import wbh.bookworm.hoerbuchkatalog.domain.bestellung.HoerbuchAufDieMerklisteGesetzt;
import wbh.bookworm.hoerbuchkatalog.domain.bestellung.HoerbuchVonDerMerklisteEntfernt;
import wbh.bookworm.hoerbuchkatalog.domain.bestellung.Merkliste;
import wbh.bookworm.hoerbuchkatalog.domain.bestellung.MerklisteId;
import wbh.bookworm.shared.domain.Hoerernummer;

import aoc.mikrokosmos.ddd.repository.JsonDomainRepository;

// TODO @DomainRepositoryComponent
public class MerklisteRepository extends JsonDomainRepository<Merkliste, MerklisteId> {

    public MerklisteRepository(final Path storagePath) {
        super(Merkliste.class, MerklisteId.class, storagePath);
        saveOnEvent(logger, HoerbuchAufDieMerklisteGesetzt.class);
        saveOnEvent(logger, HoerbuchVonDerMerklisteEntfernt.class);
    }

    private MerklisteId merklisteIdFuerHoerer(final Hoerernummer hoerernummer) {
        return new MerklisteId(hoerernummer + "-Merkliste");
    }

    public Merkliste erstellen(final Hoerernummer hoerernummer) {
        return new Merkliste(merklisteIdFuerHoerer(hoerernummer), hoerernummer);
    }

    public Optional<Merkliste> load(final Hoerernummer hoerernummer) {
        return super.load(merklisteIdFuerHoerer(hoerernummer));
    }

}

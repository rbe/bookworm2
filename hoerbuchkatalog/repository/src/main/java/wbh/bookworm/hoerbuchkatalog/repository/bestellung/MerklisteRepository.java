/*
 * Copyright (C) 2011-2018 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchkatalog.repository.bestellung;

import wbh.bookworm.hoerbuchkatalog.domain.bestellung.HoerbuechAufDieMerklisteGesetzt;
import wbh.bookworm.hoerbuchkatalog.domain.bestellung.HoerbuechVonDerMerklisteEntfernt;
import wbh.bookworm.hoerbuchkatalog.domain.bestellung.Merkliste;
import wbh.bookworm.hoerbuchkatalog.domain.bestellung.MerklisteId;
import wbh.bookworm.hoerbuchkatalog.domain.hoerer.Hoerernummer;
import wbh.bookworm.platform.ddd.repository.DomainRespositoryComponent;
import wbh.bookworm.platform.ddd.repository.JsonDomainRepository;

import java.util.Optional;

@DomainRespositoryComponent
public class MerklisteRepository extends JsonDomainRepository<Merkliste, MerklisteId> {

    public MerklisteRepository() {
        super(Merkliste.class, MerklisteId.class);
        saveOnEvent(logger, HoerbuechAufDieMerklisteGesetzt.class);
        saveOnEvent(logger, HoerbuechVonDerMerklisteEntfernt.class);
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

/*
 * Copyright (C) 2011-2018 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchkatalog.repository.bestellung;

import java.nio.file.Path;
import java.util.Set;

import wbh.bookworm.hoerbuchkatalog.domain.bestellung.Bestellung;
import wbh.bookworm.hoerbuchkatalog.domain.bestellung.BestellungAufgegeben;
import wbh.bookworm.hoerbuchkatalog.domain.bestellung.BestellungId;
import wbh.bookworm.hoerbuchkatalog.domain.hoerer.HoererEmail;
import wbh.bookworm.hoerbuchkatalog.domain.hoerer.Hoerername;
import wbh.bookworm.shared.domain.Hoerernummer;
import wbh.bookworm.shared.domain.Titelnummer;

import aoc.mikrokosmos.ddd.repository.JsonDomainRepository;

// @Bean in BestellungAppConfig
public class BestellungRepository extends JsonDomainRepository<Bestellung, BestellungId> {

    public BestellungRepository(final Path storagePath) {
        super(Bestellung.class, BestellungId.class, storagePath);
        saveOnEvent(logger, BestellungAufgegeben.class);
    }

    public Bestellung erstellen(final Hoerernummer hoerernummer,
                                final Hoerername hoerername, final HoererEmail hoereremail,
                                final String bemerkung,
                                final Boolean bestellkarteMischen,
                                final Boolean alteBestellkarteLoeschen,
                                final Set<Titelnummer> cdTitelnummern) {
        final Bestellung aggregate = new Bestellung(
                nextIdentity(hoerernummer.getValue()),
                hoerernummer, hoerername, hoereremail,
                bemerkung, bestellkarteMischen, alteBestellkarteLoeschen,
                Set.copyOf(cdTitelnummern));
        return save(aggregate);
    }

}

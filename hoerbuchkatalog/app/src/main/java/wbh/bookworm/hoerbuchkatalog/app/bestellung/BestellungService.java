/*
 * Copyright (C) 2011-2018 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchkatalog.app.bestellung;

import java.util.Collections;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import wbh.bookworm.hoerbuchkatalog.domain.bestellung.Bestellung;
import wbh.bookworm.hoerbuchkatalog.domain.bestellung.BestellungId;
import wbh.bookworm.hoerbuchkatalog.domain.bestellung.BestellungSessionId;
import wbh.bookworm.hoerbuchkatalog.domain.bestellung.CdWarenkorb;
import wbh.bookworm.hoerbuchkatalog.domain.hoerer.HoererEmail;
import wbh.bookworm.hoerbuchkatalog.domain.hoerer.Hoerername;
import wbh.bookworm.hoerbuchkatalog.repository.bestellung.BestellungRepository;
import wbh.bookworm.shared.domain.Hoerernummer;

import aoc.mikrokosmos.ddd.repository.QueryPredicate;

@Service
public final class BestellungService {

    private static final Logger LOGGER = LoggerFactory.getLogger(BestellungService.class);

    private final WarenkorbService warenkorbService;

    private final BestellungRepository bestellungRepository;

    @Autowired
    public BestellungService(final WarenkorbService warenkorbService,
                             final BestellungRepository bestellungRepository) {
        this.warenkorbService = warenkorbService;
        this.bestellungRepository = bestellungRepository;
    }

    public BestellungSessionId bestellungSessionId(final String hoerernummer) {
        final BestellungSessionId bestellungSessionId = new BestellungSessionId(hoerernummer);
        LOGGER.debug("Hörer {} hat BestellungSessionId {} bekommen", hoerernummer, bestellungSessionId);
        return bestellungSessionId;
    }

    public BestellungSessionId bestellungSessionId(final Hoerernummer hoerernummer) {
        final BestellungSessionId bestellungSessionId = new BestellungSessionId();
        LOGGER.debug("Hörer {} hat BestellungSessionId {} bekommen", hoerernummer, bestellungSessionId);
        return bestellungSessionId;
    }

    /**
     * Command
     */
    public Optional<BestellungId> bestellungAufgeben(final BestellungSessionId bestellungSessionId,
            /* TODO Hoerer */final Hoerernummer hoerernummer,
                                                     final Hoerername hoerername,
                                                     final HoererEmail hoereremail,
                                                     final String bemerkung,
                                                     final Boolean bestellkarteMischen,
                                                     final Boolean alteBestellkarteLoeschen) {
        LOGGER.trace("Bestellung {} für Hörer {} wird aufgegeben", this, hoerernummer);
        final CdWarenkorb cdWarenkorb = warenkorbService
                .cdWarenkorbKopie(bestellungSessionId, hoerernummer);
        final Bestellung bestellung = bestellungRepository.erstellen(
                hoerernummer, hoerername, hoereremail,
                bemerkung, bestellkarteMischen, alteBestellkarteLoeschen,
                cdWarenkorb.getTitelnummern());
        bestellung.aufgeben();
        cdWarenkorb.leeren();
        LOGGER.info("Bestellung {} für Hörer {} wurde erfolgreich aufgegeben!",
                bestellung, hoerernummer);
        return Optional.of(bestellung.getDomainId());
    }

    public long anzahlBestellungen(final Hoerernummer hoerernummer) {
        return bestellungRepository
                .find(/*hoerernummer.getValue(),
                 */QueryPredicate.Equals.of("hoerernummer", hoerernummer.getValue()))
                .orElseGet(Collections::emptySet)
                .size();
    }

}

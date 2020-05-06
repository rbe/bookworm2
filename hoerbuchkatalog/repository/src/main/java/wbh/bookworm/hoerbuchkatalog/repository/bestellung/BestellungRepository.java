/*
 * Copyright (C) 2011-2018 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchkatalog.repository.bestellung;

import java.nio.file.Path;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

import wbh.bookworm.hoerbuchkatalog.domain.bestellung.Bestellung;
import wbh.bookworm.hoerbuchkatalog.domain.bestellung.BestellungAufgegeben;
import wbh.bookworm.hoerbuchkatalog.domain.bestellung.BestellungId;
import wbh.bookworm.hoerbuchkatalog.domain.hoerer.HoererEmail;
import wbh.bookworm.hoerbuchkatalog.domain.hoerer.Hoerername;
import wbh.bookworm.shared.domain.hoerbuch.Titelnummer;
import wbh.bookworm.shared.domain.hoerer.Hoerernummer;

import aoc.mikrokosmos.ddd.repository.JsonDomainRepository;
import aoc.mikrokosmos.ddd.repository.QueryPredicate;

// @Bean in BestellungAppConfig
public class BestellungRepository extends JsonDomainRepository<Bestellung, BestellungId> {

    private static final String HOERERNUMMER = "hoerernummer";

    public BestellungRepository(final Path storagePath) {
        super(Bestellung.class, BestellungId.class, storagePath);
        saveOnEvent(logger, BestellungAufgegeben.class);
    }

    public Bestellung erstellen(final Hoerernummer hoerernummer,
                                final Hoerername hoerername, final HoererEmail hoereremail,
                                final String bemerkung,
                                final Boolean bestellkarteMischen,
                                final Boolean alteBestellkarteLoeschen,
                                final Set<Titelnummer> cdTitelnummern,
                                final Set<Titelnummer> downloadTitelnummern) {
        final Bestellung aggregate = new Bestellung(
                nextIdentity(hoerernummer.getValue()),
                hoerernummer, hoerername, hoereremail,
                bemerkung, bestellkarteMischen, alteBestellkarteLoeschen,
                Set.copyOf(cdTitelnummern),
                Set.copyOf(downloadTitelnummern));
        return save(aggregate);
    }

    // TODO Überlegen: Kann auch aus der blista Abfrage interpretiert werden!
    private Set<Bestellung> alleMitDownloads(final Hoerernummer hoerernummer) {
        return find(QueryPredicate.Equals.of(HOERERNUMMER, hoerernummer.getValue()))
                .orElseGet(Collections::emptySet)
                .stream()
                .filter(Bestellung::hatDownloadTitelnummern)
                .collect(Collectors.toSet());
    }

    public long countAlleDownloads(final Hoerernummer hoerernummer) {
        return alleMitDownloads(hoerernummer)
                .stream()
                .map(Bestellung::getDownloadTitelnummern)
                .mapToLong(Collection::size)
                .sum();
    }

    // TODO Überlegen: Kann auch aus der blista Abfrage interpretiert werden!
    private Set<Bestellung> alleVonHeuteMitDownloads(final Hoerernummer hoerernummer) {
        return find(QueryPredicate.Equals.of(HOERERNUMMER, hoerernummer.getValue()))
                .orElseGet(Collections::emptySet)
                .stream()
                .filter(Bestellung::heuteAbgeschickt)
                .filter(Bestellung::hatDownloadTitelnummern)
                .collect(Collectors.toSet());
    }

    // TODO Überlegen: Kann auch aus der blista Abfrage interpretiert werden!
    public long countAlleDownloadsVonHeute(final Hoerernummer hoerernummer) {
        return alleVonHeuteMitDownloads(hoerernummer)
                .stream()
                .map(Bestellung::getDownloadTitelnummern)
                .mapToLong(Collection::size)
                .sum();
    }

    // TODO Überlegen: Kann auch aus der blista Abfrage interpretiert werden!
    private Set<Bestellung> alleInDiesemMonatMitDownloads(final Hoerernummer hoerernummer) {
        return find(QueryPredicate.Equals.of(HOERERNUMMER, hoerernummer.getValue()))
                .orElseGet(Collections::emptySet)
                .stream()
                .filter(Bestellung::inAktuellemMonatAbgeschickt)
                .filter(Bestellung::hatDownloadTitelnummern)
                .collect(Collectors.toSet());
    }

    // TODO Überlegen: Kann auch aus der blista Abfrage interpretiert werden!
    public long countAlleDownloadsInDiesemMonat(final Hoerernummer hoerernummer) {
        return alleInDiesemMonatMitDownloads(hoerernummer)
                .stream()
                .map(Bestellung::getDownloadTitelnummern)
                .mapToLong(Collection::size)
                .sum();
    }

}

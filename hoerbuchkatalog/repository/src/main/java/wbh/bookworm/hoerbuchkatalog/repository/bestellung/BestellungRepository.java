/*
 * Copyright (C) 2011-2018 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchkatalog.repository.bestellung;

import wbh.bookworm.hoerbuchkatalog.domain.bestellung.Bestellung;
import wbh.bookworm.hoerbuchkatalog.domain.bestellung.BestellungAufgegeben;
import wbh.bookworm.hoerbuchkatalog.domain.bestellung.BestellungId;
import wbh.bookworm.hoerbuchkatalog.domain.hoerer.HoererEmail;
import wbh.bookworm.hoerbuchkatalog.domain.hoerer.Hoerername;
import wbh.bookworm.hoerbuchkatalog.domain.hoerer.Hoerernummer;
import wbh.bookworm.hoerbuchkatalog.domain.katalog.Titelnummer;

import aoc.ddd.repository.DomainRespositoryComponent;
import aoc.ddd.repository.JsonDomainRepository;
import aoc.ddd.repository.QueryPredicate;

import java.nio.file.Path;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

@DomainRespositoryComponent
public class BestellungRepository extends JsonDomainRepository<Bestellung, BestellungId> {

    public BestellungRepository(final Path storagePath) {
        super(Bestellung.class, BestellungId.class, storagePath);
        saveOnEvent(logger, BestellungAufgegeben.class);
    }

    public Bestellung erstellen(final Hoerernummer hoerernummer,
                                final Hoerername hoerername, final HoererEmail hoereremail,
                                final String bemerkung,
                                final Boolean bestellkarteMischen, final Boolean alteBestellkarteLoeschen,
                                final Set<Titelnummer> cdTitelnummern, final Set<Titelnummer> downloadTitelnummern) {
        final Bestellung aggregate = new Bestellung(
                nextIdentity(hoerernummer.getValue()),
                hoerernummer, hoerername, hoereremail,
                bemerkung,
                bestellkarteMischen, alteBestellkarteLoeschen,
                cdTitelnummern, downloadTitelnummern);
        return save(aggregate);
    }

    public Set<Bestellung> alleMitDownloads(final Hoerernummer hoerernummer) {
        return find(QueryPredicate.Equals.of("hoerernummer", hoerernummer.getValue()))
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

    public Set<Bestellung> alleVonHeuteMitDownloads(final Hoerernummer hoerernummer) {
        return find(QueryPredicate.Equals.of("hoerernummer", hoerernummer.getValue()))
                .orElseGet(Collections::emptySet)
                .stream()
                .filter(Bestellung::heuteAbgeschickt)
                .filter(Bestellung::hatDownloadTitelnummern)
                .collect(Collectors.toSet());
    }

    public long countAlleDownloadsVonHeute(final Hoerernummer hoerernummer) {
        return alleVonHeuteMitDownloads(hoerernummer)
                .stream()
                .map(Bestellung::getDownloadTitelnummern)
                .mapToLong(Collection::size)
                .sum();
    }

    public Set<Bestellung> alleInDiesemMonatMitDownloads(final Hoerernummer hoerernummer) {
        return find(QueryPredicate.Equals.of("hoerernummer", hoerernummer.getValue()))
                .orElseGet(Collections::emptySet)
                .stream()
                .filter(Bestellung::inAktuellemMonatAbgeschickt)
                .filter(Bestellung::hatDownloadTitelnummern)
                .collect(Collectors.toSet());
    }

    public long countAlleDownloadsInDiesemMonat(final Hoerernummer hoerernummer) {
        return alleInDiesemMonatMitDownloads(hoerernummer)
                .stream()
                .map(Bestellung::getDownloadTitelnummern)
                .mapToLong(Collection::size)
                .sum();
    }

}

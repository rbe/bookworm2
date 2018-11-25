/*
 * Copyright (C) 2011-2018 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchkatalog.domain.lieferung;

import wbh.bookworm.hoerbuchkatalog.domain.hoerer.Hoerernummer;

import aoc.ddd.model.DomainValueObject;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public final class HoererBlistaDownloads extends DomainValueObject {

    private final Hoerernummer hoerernummer;

    private final LocalDateTime standVom;

    private final List<BlistaDownload> blistaDownload;

    // TODO Fehlermeldung

    public HoererBlistaDownloads(final Hoerernummer hoerernummer,
                                 final List<BlistaDownload> blistaDownload) {
        this.hoerernummer = hoerernummer;
        this.blistaDownload = blistaDownload;
        blistaDownload.sort(Comparator.comparing(BlistaDownload::getBestelldatum).reversed());
        this.standVom = LocalDateTime.now();
    }

    public Hoerernummer getHoerernummer() {
        return hoerernummer;
    }

    public LocalDateTime getStandVom() {
        return standVom;
    }

    public List<BlistaDownload> alle() {
        return blistaDownload;
    }

    public List<BlistaDownload> bezuegsfaehige() {
        return blistaDownload.stream()
                .filter(BlistaDownload::isBezugsfaehig)
                .collect(Collectors.toList());
    }

    @Override
    public String toString() {
        return String.format("HoererBlistaDownloads{hoerernummer=%s, blistaDownload=%s, standVom=%s}",
                hoerernummer, blistaDownload, standVom);
    }

}

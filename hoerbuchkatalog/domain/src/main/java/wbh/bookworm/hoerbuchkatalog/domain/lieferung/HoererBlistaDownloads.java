/*
 * Copyright (C) 2011-2018 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchkatalog.domain.lieferung;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import wbh.bookworm.shared.domain.Hoerernummer;

import aoc.mikrokosmos.ddd.model.DomainValueObject;

public final class HoererBlistaDownloads extends DomainValueObject {

    private final Hoerernummer hoerernummer;

    private final LocalDateTime standVom;

    private final List<BlistaDownload> blistaDownload;

    private final String fehlercode;

    private final String fehlermeldung;

    private HoererBlistaDownloads(final Hoerernummer hoerernummer,
                                  final List<BlistaDownload> blistaDownload,
                                  final String fehlercode, final String fehlermeldung) {
        Objects.requireNonNull(hoerernummer);
        this.hoerernummer = hoerernummer;
        Objects.requireNonNull(blistaDownload);
        this.blistaDownload = blistaDownload;
        if (!blistaDownload.isEmpty()) {
            blistaDownload.sort(
                    Comparator.comparing(BlistaDownload::getBestelldatum)
                            .reversed());
        }
        this.standVom = LocalDateTime.now();
        this.fehlercode = fehlercode;
        this.fehlermeldung = fehlermeldung;
    }

    public HoererBlistaDownloads(final Hoerernummer hoerernummer,
                                 final List<BlistaDownload> blistaDownload) {
        this(hoerernummer, blistaDownload, "", "");
    }

    public HoererBlistaDownloads(final Hoerernummer hoerernummer,
                                 final String fehlercode, final String fehlermeldung) {
        this(hoerernummer, Collections.emptyList(), fehlercode, fehlermeldung);
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
                //.filter(BlistaDownload::isBezugsfaehig)
                .collect(Collectors.toList());
    }

    public boolean hatFehler() {
        return (null != fehlercode && !fehlercode.isBlank())
                || (null != fehlermeldung && !fehlermeldung.isBlank());
    }

    public String getFehlercode() {
        return fehlercode;
    }

    public String getFehlermeldung() {
        return fehlermeldung;
    }

    @Override
    public String toString() {
        return String.format("HoererBlistaDownloads{hoerernummer=%s, blistaDownload=%s, standVom=%s}",
                hoerernummer, blistaDownload, standVom);
    }

}

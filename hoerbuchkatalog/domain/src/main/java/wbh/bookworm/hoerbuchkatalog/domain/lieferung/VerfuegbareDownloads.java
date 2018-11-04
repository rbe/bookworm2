/*
 * Copyright (C) 2011-2018 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchkatalog.domain.lieferung;

import wbh.bookworm.hoerbuchkatalog.domain.hoerer.Hoerernummer;
import wbh.bookworm.platform.ddd.model.DomainValueObject;

import java.time.LocalDateTime;
import java.util.List;

public class VerfuegbareDownloads extends DomainValueObject {

    private final Hoerernummer hoerernummer;

    private final List<BlistaDlsDownload> bereitgestellteDownloads;

    private final LocalDateTime standVom;

    public VerfuegbareDownloads(final Hoerernummer hoerernummer,
                                final List<BlistaDlsDownload> bereitgestellteDownloads) {
        this.hoerernummer = hoerernummer;
        this.bereitgestellteDownloads = bereitgestellteDownloads;
        this.standVom = LocalDateTime.now();
    }

    public Hoerernummer getHoerernummer() {
        return hoerernummer;
    }

    public List<BlistaDlsDownload> getBereitgestellteDownloads() {
        return bereitgestellteDownloads;
    }

    public LocalDateTime getStandVom() {
        return standVom;
    }

    @Override
    public String toString() {
        return String.format("VerfuegbareDownloads{hoerernummer=%s, bereitgestellteDownloads=%s, standVom=%s}",
                hoerernummer, bereitgestellteDownloads, standVom);
    }

}

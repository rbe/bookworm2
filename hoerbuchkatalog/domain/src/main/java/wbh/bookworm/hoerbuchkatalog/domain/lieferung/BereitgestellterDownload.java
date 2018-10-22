/*
 * Copyright (C) 2011-2018 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchkatalog.domain.lieferung;

import wbh.bookworm.hoerbuchkatalog.domain.hoerer.Hoerernummer;
import wbh.bookworm.hoerbuchkatalog.domain.katalog.AghNummer;

import java.time.LocalDateTime;
import java.util.Objects;

public final class BereitgestellterDownload {

    private final Hoerernummer hoerernummer;

    private final AghNummer aghNummer;

    private boolean bezugsfaehig;

    private final LocalDateTime bestelldatum;

    private final LocalDateTime rueckgabedatum;

    private final int downloadCount;

    private final int maxDownload;

    private final String downloadLink;

    public BereitgestellterDownload(final Hoerernummer hoerernummer,
                                    final AghNummer aghNummer,
                                    final int ausleihstatus,
                                    final LocalDateTime bestelldatum, final LocalDateTime rueckgabedatum,
                                    final String dlsDescription,
                                    final int downloadCount, final int maxDownload,
                                    final String downloadLink) {
        Objects.requireNonNull(hoerernummer);
        this.hoerernummer = hoerernummer;
        Objects.requireNonNull(aghNummer);
        this.aghNummer = aghNummer;
        Objects.requireNonNull(dlsDescription);
        bezugsfaehig = false;
        if (ausleihstatus >= 0) {
            switch (ausleihstatus) {
                case 0:
                    if (dlsDescription.contains("doppelt bestellt")) {
                        bezugsfaehig = true;
                    }
                    break;
                case 3:
                    bezugsfaehig = true;
                    break;
            }
        }
        this.bestelldatum = bestelldatum;
        this.rueckgabedatum = rueckgabedatum;
        this.downloadCount = downloadCount;
        this.maxDownload = maxDownload;
        this.downloadLink = downloadLink;
    }

}

/*
 * Copyright (C) 2011-2018 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchkatalog.domain.lieferung;

import wbh.bookworm.hoerbuchkatalog.domain.hoerer.Hoerernummer;
import wbh.bookworm.hoerbuchkatalog.domain.katalog.AghNummer;

import aoc.ddd.model.DomainValueObject;

import java.time.LocalDateTime;
import java.util.Objects;

public final class BlistaDlsDownload extends DomainValueObject {

    private final Hoerernummer hoerernummer;

    private final AghNummer aghNummer;

    private int ausleihstatus;

    private boolean bezugsfaehig;

    private final LocalDateTime bestelldatum;

    private final LocalDateTime rueckgabedatum;

    private final int downloadCount;

    private final int maxDownload;

    private final String downloadLink;

    public BlistaDlsDownload(final Hoerernummer hoerernummer,
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
        this.ausleihstatus = ausleihstatus;
        bezugsfaehig = false;
        switch (ausleihstatus) {
            case 0:
                if (dlsDescription.indexOf("doppelt bestellt") > 0) {
                    bezugsfaehig = true;
                    break;
                }
            case 1:
            case 2:
            case 4:
                bezugsfaehig = false;
                break;
            case 3:
                bezugsfaehig = true;
                break;
        }
        this.bestelldatum = bestelldatum;
        this.rueckgabedatum = rueckgabedatum;
        this.downloadCount = downloadCount;
        this.maxDownload = maxDownload;
        this.downloadLink = downloadLink;
    }

    public Hoerernummer getHoerernummer() {
        return hoerernummer;
    }

    public AghNummer getAghNummer() {
        return aghNummer;
    }

    public boolean isBezugsfaehig() {
        return bezugsfaehig;
    }

    public LocalDateTime getBestelldatum() {
        return bestelldatum;
    }

    public LocalDateTime getRueckgabedatum() {
        return rueckgabedatum;
    }

    public int getDownloadCount() {
        return downloadCount;
    }

    public int getMaxDownload() {
        return maxDownload;
    }

    public String getDownloadLink() {
        return downloadLink;
    }

    @Override
    public String toString() {
        return String.format("BlistaDlsDownload{hoerernummer=%s, aghNummer=%s," +
                        " ausleihstatus=%d, bezugsfaehig=%s, bestelldatum=%s, rueckgabedatum=%s," +
                        " downloadCount=%d, maxDownload=%d," +
                        " downloadLink='%s'}",
                hoerernummer, aghNummer,
                ausleihstatus, bezugsfaehig, bestelldatum, rueckgabedatum,
                downloadCount, maxDownload,
                downloadLink);
    }

}

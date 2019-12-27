/*
 * Copyright (C) 2011-2018 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchkatalog.domain.lieferung;

import wbh.bookworm.hoerbuchkatalog.domain.hoerer.Hoerernummer;
import wbh.bookworm.hoerbuchkatalog.domain.katalog.AghNummer;
import wbh.bookworm.hoerbuchkatalog.domain.katalog.Hoerbuch;
import wbh.bookworm.hoerbuchkatalog.domain.katalog.Titelnummer;

import aoc.mikrokosmos.ddd.model.DomainValueObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public final class BlistaDownload extends DomainValueObject {

    private static final Logger LOGGER = LoggerFactory.getLogger(BlistaDownload.class);

    private final Hoerernummer hoerernummer;

    private final AghNummer aghNummer;

    private final String statusText;

    private final Titelnummer titelnummer;

    private final String titel;

    private final String autor;

    private final String spieldauer;

    private final int ausleihstatus;

    private boolean bezugsfaehig;

    private final LocalDateTime bestelldatum;

    private final LocalDateTime rueckgabedatum;

    private /*final*/ int downloadCount;

    private final int maxDownload;

    private final String downloadLink;

    private final int gesperrt;

    public BlistaDownload(final Hoerernummer hoerernummer,
                          final AghNummer aghNummer,
                          final Titelnummer titelnummer, final String titel,
                          final String autor, final String spieldauer,
                          final int ausleihstatus,
                          final LocalDateTime bestelldatum, final LocalDateTime rueckgabedatum,
                          final String dlsDescription,
                          final int downloadCount, final int maxDownload,
                          final String downloadLink, final int gesperrt) {
        Objects.requireNonNull(hoerernummer);
        this.hoerernummer = hoerernummer;
        Objects.requireNonNull(aghNummer);
        this.aghNummer = aghNummer;
        Objects.requireNonNull(titelnummer);
        this.titelnummer = titelnummer;
        Objects.requireNonNull(titel);
        this.titel = titel;
        Objects.requireNonNull(autor);
        this.autor = autor;
        Objects.requireNonNull(spieldauer);
        this.spieldauer = spieldauer;
        Objects.requireNonNull(dlsDescription);
        this.ausleihstatus = ausleihstatus;
        if (LocalDateTime.now().isAfter(rueckgabedatum)) {
            statusText = String.format("%d: Ausleihzeitraum abgelaufen", ausleihstatus);
        } else {
            statusText = String.format("%d: %s", ausleihstatus
                    , dlsDescription);
        }
        bezugsfaehig = false;
        switch (ausleihstatus) {
            case 0:
                /*if (dlsDescription.contains("doppelt bestellt")) {
                    bezugsfaehig = true;
                }
                break;*/
            case 1:
            case 2:
            case 4:
                break;
            case 3:
                bezugsfaehig = true;
                break;
            default:
                LOGGER.warn("AGH Nummer {}: Unbekannter Ausleihstatus {}",
                        aghNummer, ausleihstatus);
        }
        this.bestelldatum = bestelldatum;
        this.rueckgabedatum = rueckgabedatum;
        this.downloadCount = downloadCount;
        this.maxDownload = maxDownload;
        this.downloadLink = downloadLink;
        this.gesperrt = gesperrt;
        bezugsfaehig &= /* TODO isUrl(String) */
                gesperrt == 0
                        && null != downloadLink && !downloadLink.isBlank();
    }

    public static BlistaDownload of(final Hoerernummer hoerernummer,
                                    final Hoerbuch hoerbuch,
                                    final int ausleihstatus,
                                    final LocalDateTime bestelldatum, final LocalDateTime rueckgabedatum,
                                    final String dlsDescription,
                                    final int downloadCount, final int maxDownload,
                                    final String downloadLink, final int gesperrt) {
        return new BlistaDownload(hoerernummer, hoerbuch.getAghNummer(),
                hoerbuch.getTitelnummer(), hoerbuch.getTitel(),
                hoerbuch.getAutor(), hoerbuch.getSpieldauer(),
                ausleihstatus,
                bestelldatum, rueckgabedatum,
                dlsDescription,
                downloadCount, maxDownload,
                downloadLink, gesperrt);
    }

    public Hoerernummer getHoerernummer() {
        return hoerernummer;
    }

    public AghNummer getAghNummer() {
        return aghNummer;
    }

    public Titelnummer getTitelnummer() {
        return titelnummer;
    }

    public String getTitel() {
        return titel;
    }

    public String getAutor() {
        return autor;
    }

    public String getSpieldauer() {
        return spieldauer;
    }

    public boolean isBezugsfaehig() {
        return bezugsfaehig;
    }

    public String getStatusText() {
        return statusText;
    }

    public LocalDateTime getBestelldatum() {
        return bestelldatum;
    }

    public String getBestelldatumAufDeutsch() {
        return null != bestelldatum
                ? bestelldatum.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"))
                : "";
    }

    public LocalDateTime getRueckgabedatum() {
        return rueckgabedatum;
    }

    public int getAusleihstatus() {
        return ausleihstatus;
    }

    public int getDownloadCount() {
        return downloadCount;
    }

    public int getMaxDownload() {
        return maxDownload;
    }

    public int getGetaetigteDownloads() {
        return maxDownload - downloadCount;
    }

    public String getDownloadLink() {
        return downloadLink;
    }

    public int getGesperrt() {
        return gesperrt;
    }

    public void downloadCounterRuntersetzen() {
        downloadCount++;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final BlistaDownload that = (BlistaDownload) o;
        return ausleihstatus == that.ausleihstatus &&
                hoerernummer.equals(that.hoerernummer) &&
                aghNummer.equals(that.aghNummer) &&
                titelnummer.equals(that.titelnummer) &&
                bestelldatum.equals(that.bestelldatum) &&
                rueckgabedatum.equals(that.rueckgabedatum) &&
                downloadLink.equals(that.downloadLink);
    }

    @Override
    public int hashCode() {
        return Objects.hash(hoerernummer, aghNummer, titelnummer, ausleihstatus, bestelldatum, rueckgabedatum, downloadLink);
    }

    @Override
    public String toString() {
        return String.format("BlistaDownload{hoerernummer'%s', titelnummer='%s', aghNummer='%s'," +
                        " ausleihstatus=%d, bezugsfaehig='%s', bestelldatum='%s', rueckgabedatum='%s'," +
                        " downloadCount=%d, maxDownload=%d," +
                        " downloadLink='%s', gesperrt=%d}",
                hoerernummer, titelnummer, aghNummer,
                ausleihstatus, bezugsfaehig, bestelldatum, rueckgabedatum,
                downloadCount, maxDownload,
                downloadLink, gesperrt);
    }

}

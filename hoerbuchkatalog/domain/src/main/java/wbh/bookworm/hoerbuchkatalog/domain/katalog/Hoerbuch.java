/*
 * Copyright (C) 2011-2018 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchkatalog.domain.katalog;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import wbh.bookworm.shared.domain.AghNummer;
import wbh.bookworm.shared.domain.Sachgebiet;
import wbh.bookworm.shared.domain.Titelnummer;

import aoc.mikrokosmos.ddd.model.DomainEntity;

/**
 * Entity
 */
public final class Hoerbuch extends DomainEntity<Hoerbuch, Titelnummer> {

    private static final long serialVersionUID = -1L;

    private static final Logger LOGGER = LoggerFactory.getLogger(Hoerbuch.class);

    public static final String TITELNUMMER_UNBEKANNT = "000000";

    public static final String TITEL_UNBEKANNT = "Titel unbekannt";

    private Titelnummer titelnummer; // 6 0 (nummerisch)

    private Sachgebiet sachgebiet;

    private String autor;

    private String titel;

    private String untertitel;

    private String erlaeuterung;

    private String verlagsort;

    private String verlag;

    private String druckjahr;

    private String sprecher1;

    private String sprecher2;

    private String spieldauer; // 52 Stunde, Minuten

    private String prodOrt;

    private String prodJahr;

    private String suchwoerter;

    private String anzahlCD;

    private String titelfamilie;

    private LocalDate einstelldatum;

    private AghNummer aghNummer;

    private boolean downloadbar;

    public Hoerbuch(final Sachgebiet sachgebiet,
                    final Titelnummer titelnummer, final String autor,
                    final String titel,
                    final String untertitel, final String erlaeuterung, final String verlagsort,
                    final String verlag, final String druckjahr, final String sprecher1,
                    final String sprecher2, final String spieldauer,
                    final String prodOrt, final String prodJahr, final String suchwoerter,
                    final String anzahlCD, final String titelfamilie, final LocalDate einstelldatum,
                    final AghNummer aghNummer, final boolean downloadbar) {
        super(titelnummer);
        this.titelnummer = titelnummer;
        this.sachgebiet = sachgebiet;
        this.autor = autor;
        this.titel = titel;
        this.untertitel = untertitel;
        this.erlaeuterung = erlaeuterung;
        this.verlagsort = verlagsort;
        this.verlag = verlag;
        this.druckjahr = druckjahr;
        this.sprecher1 = sprecher1;
        this.sprecher2 = sprecher2;
        this.spieldauer = spieldauer;
        this.prodOrt = prodOrt;
        this.prodJahr = prodJahr;
        this.suchwoerter = suchwoerter;
        this.anzahlCD = anzahlCD;
        this.titelfamilie = titelfamilie;
        this.einstelldatum = einstelldatum;
        this.aghNummer = aghNummer;
        this.downloadbar = downloadbar;
    }

    public Hoerbuch(final Sachgebiet sachgebiet,
                    final Titelnummer titelnummer, final String autor,
                    final String titel,
                    final String untertitel, final String erlaeuterung, final String verlagsort,
                    final String verlag, final String druckjahr, final String sprecher1,
                    final String sprecher2, final String spieldauer,
                    final String prodOrt, final String prodJahr, final String suchwoerter,
                    final String anzahlCD, final String titelfamilie, final String einstelldatum,
                    final AghNummer aghNummer, final boolean downloadbar) {
        super(titelnummer);
        this.titelnummer = titelnummer;
        this.sachgebiet = sachgebiet;
        this.autor = autor;
        this.titel = titel;
        this.untertitel = untertitel;
        this.erlaeuterung = erlaeuterung;
        this.verlagsort = verlagsort;
        this.verlag = verlag;
        this.druckjahr = druckjahr;
        this.sprecher1 = sprecher1;
        this.sprecher2 = sprecher2;
        this.spieldauer = spieldauer;
        this.prodOrt = prodOrt;
        this.prodJahr = prodJahr;
        this.suchwoerter = suchwoerter;
        this.anzahlCD = anzahlCD;
        this.titelfamilie = titelfamilie;
        setEinstelldatum(einstelldatum);
        this.aghNummer = aghNummer;
        this.downloadbar = downloadbar;
    }

    private static boolean isNotBlank(final String str) {
        return null != str && !str.isBlank();
    }

    public static Hoerbuch unbekannt(final Titelnummer titelnummer) {
        return new Hoerbuch(Sachgebiet.NA,
                titelnummer,
                "",
                TITEL_UNBEKANNT, "",
                "",
                "", "", "",
                "", "", "0,00",
                "", "",
                "",
                "0",
                "",
                "",
                null, false);
    }

    public static Hoerbuch unbekannt(final AghNummer aghNummer) {
        return new Hoerbuch(Sachgebiet.NA,
                new Titelnummer(TITELNUMMER_UNBEKANNT),
                "",
                TITEL_UNBEKANNT, "",
                "",
                "", "", "",
                "", "", "0,00",
                "", "",
                "",
                "0",
                "",
                "",
                aghNummer, true);
    }

    public static Hoerbuch unbekannt(final Titelnummer titelnummer, final AghNummer aghNummer,
                                     final String autor, final String titel) {
        return new Hoerbuch(Sachgebiet.NA,
                null != titelnummer ? titelnummer : new Titelnummer(TITELNUMMER_UNBEKANNT),
                null != autor ? autor : "",
                null != titel ? titel : "",
                "",
                "",
                "", "", "",
                "", "", "0,00",
                "", "",
                "",
                "0",
                "",
                "",
                aghNummer, false);
    }

    public static Hoerbuch unbekannterDownload(final AghNummer aghNummer, final String titel) {
        return new Hoerbuch(Sachgebiet.NA,
                new Titelnummer(TITELNUMMER_UNBEKANNT),
                "",
                null != titel ? titel : "",
                "",
                "",
                "", "", "",
                "", "", "0,00",
                "", "",
                "",
                "0",
                "",
                "",
                aghNummer, false);
    }

    public boolean isUnbekannt() {
        return titel.equals(TITEL_UNBEKANNT);
    }

    public Sachgebiet getSachgebiet() {
        return null != sachgebiet ? sachgebiet : Sachgebiet.NA;
    }

    public void setSachgebiet(Sachgebiet sachgebiet) {
        this.sachgebiet = sachgebiet;
    }

    public void setSachgebiet(char sachgebiet) {
        this.sachgebiet = Sachgebiet.valueOf(String.valueOf(sachgebiet));
    }

    public Titelnummer getTitelnummer() {
        return titelnummer;
    }

    public void setTitelnummer(Titelnummer titelnummer) {
        this.titelnummer = titelnummer;
    }

    public void setTitelnummer(String titelnummer) {
        this.titelnummer = new Titelnummer(titelnummer);
    }

    public String getAutor() {
        return isNotBlank(autor) ? autor : "";
    }

    public void setAutor(String autor) {
        this.autor = autor;
    }

    public String getTitel() {
        return isNotBlank(titel) ? titel : "Titel leider unbekannt";
    }

    public void setTitel(String titel) {
        this.titel = titel;
    }

    public String getUntertitel() {
        return isNotBlank(untertitel) ? untertitel : "";
    }

    public String getErlaeuterung() {
        return isNotBlank(erlaeuterung) ? erlaeuterung : "";
    }

    public String getVerlagsort() {
        return isNotBlank(verlagsort) ? verlagsort : "";
    }

    public String getVerlag() {
        return isNotBlank(verlag) ? verlag : "";
    }

    public String getDruckjahr() {
        return isNotBlank(druckjahr) ? druckjahr : "";
    }

    public String getSprecher1() {
        return isNotBlank(sprecher1) ? sprecher1 : "";
    }

    public String getSprecher2() {
        return isNotBlank(sprecher2) ? sprecher2 : "";
    }

    public String getSprecher() {
        final StringBuilder builder = new StringBuilder();
        if (isNotBlank(sprecher1)) {
            builder.append(sprecher1);
        }
        if (isNotBlank(sprecher2)) {
            if (isNotBlank(sprecher1)) {
                builder.append(", ");
            }
            builder.append(sprecher2);
        }
        return builder.toString();
    }

    public String getSpieldauer() {
        String abgeleiteteSpieldauer = "0,00";
        final boolean spieldauerParsbar = isNotBlank(spieldauer)
                && (spieldauer.contains(",") || spieldauer.contains("."));
        if (spieldauerParsbar) {
            final String[] parts = spieldauer.split("[.,]");
            if ("00".equals(parts[1])) {
                abgeleiteteSpieldauer = String.format("%s Stunden", parts[0]);
            } else {
                abgeleiteteSpieldauer = String.format("%s Stunden %s Minuten", parts[0], parts[1]);
            }
        }
        if ("0,00".equalsIgnoreCase(spieldauer)) {
            LOGGER.warn("Spieldauer ({}) von Hörbuch {} unbekannt", spieldauer, titelnummer);
        }
        return abgeleiteteSpieldauer;
    }

    public String getProdOrt() {
        return isNotBlank(prodOrt) ? prodOrt : "";
    }

    public String getProdJahr() {
        return isNotBlank(prodJahr) ? prodJahr : "";
    }

    public String getSuchwoerter() {
        return isNotBlank(suchwoerter) ? suchwoerter : "";
    }

    public String getAnzahlCD() {
        return isNotBlank(anzahlCD) ? anzahlCD : "";
    }

    public String getTitelfamilie() {
        return isNotBlank(titelfamilie) ? titelfamilie : "";
    }

    public LocalDate getEinstelldatum() {
        return einstelldatum;
    }

    public String getEinstelldatumAufDeutsch() {
        return null != einstelldatum
                ? einstelldatum.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"))
                : "";
    }

    private void setEinstelldatum(final String einstelldatum) {
        final boolean einstelldatumGesetzt = null != einstelldatum
                && !einstelldatum.isBlank()
                && !"0".equals(einstelldatum);
        if (einstelldatumGesetzt) {
            try {
                this.einstelldatum = LocalDate.parse(einstelldatum, DateTimeFormatter.BASIC_ISO_DATE);
            } catch (DateTimeParseException e) {
                throw new IllegalArgumentException(einstelldatum, e);
            }
        }
    }

    public AghNummer getAghNummer() {
        return aghNummer;
    }

    public boolean hatAghNummer(final AghNummer aghNummer) {
        Objects.requireNonNull(aghNummer);
        return null != this.aghNummer && this.aghNummer.equals(aghNummer);
    }

    public boolean isDownloadbar() {
        return downloadbar;
    }

    public void imDownloadKatalogVorhanden() {
        downloadbar = true;
    }

    public void nichtDownloadKatalogVorhanden() {
        downloadbar = false;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final Hoerbuch hoerbuch = (Hoerbuch) o;
        return Objects.equals(titelnummer, hoerbuch.titelnummer);
    }

    @Override
    public int hashCode() {
        return Objects.hash(titelnummer);
    }

    @Override
    public int compareTo(final Hoerbuch o) {
        return o.titelnummer.getValue().compareTo(this.titelnummer.getValue());
    }

    @Override
    public String toString() {
        return String.format("Hoerbuch{titelnummer='%s', aghNummer='%s', sachgebiet='%s', autor='%s', titel='%s' untertitel='%s' erlaeuterung='%s' suchwoerter='%s'}",
                titelnummer, aghNummer, sachgebiet, autor, titel, untertitel, erlaeuterung, suchwoerter);
    }

}

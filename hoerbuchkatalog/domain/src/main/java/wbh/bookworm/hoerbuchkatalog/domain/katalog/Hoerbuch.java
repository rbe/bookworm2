/*
 * Copyright (C) 2011-2018 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchkatalog.domain.katalog;

import aoc.ddd.model.DomainEntity;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Objects;

/**
 * Entity
 */
public final class Hoerbuch extends DomainEntity<Hoerbuch, Titelnummer> {

    private static final long serialVersionUID = -1L;

    private static final Logger LOGGER = LoggerFactory.getLogger(Hoerbuch.class);

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

    private static boolean isBlank(final String str) {
        return null != str && !str.isBlank();
    }

    public static Hoerbuch unbekannt(final Titelnummer titelnummer) {
        return new Hoerbuch(Sachgebiet.NA,
                titelnummer,
                "",
                "Titel unbekannt", "",
                "",
                "", "", "",
                "", "", "0,00",
                "", "",
                "",
                "0",
                "",
                "", null, false);
    }

    public boolean isUnbekannt() {
        return titel.equals("Titel unbekannt");
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
        return !isBlank(autor) ? autor : "Autor leider unbekannt";
    }

    public void setAutor(String autor) {
        this.autor = autor;
    }

    public String getTitel() {
        return !isBlank(titel) ? titel : "Titel leider unbekannt";
    }

    public void setTitel(String titel) {
        this.titel = titel;
    }

    public String getUntertitel() {
        return !isBlank(untertitel) ? untertitel : "";
    }

    public String getErlaeuterung() {
        return !isBlank(erlaeuterung) ? erlaeuterung : "";
    }

    public String getVerlagsort() {
        return isBlank(verlagsort) ? verlagsort : "";
    }

    public String getVerlag() {
        return isBlank(verlag) ? verlag : "";
    }

    public String getDruckjahr() {
        return isBlank(druckjahr) ? druckjahr : "";
    }

    public String getSprecher1() {
        return isBlank(sprecher1) ? sprecher1 : "";
    }

    public String getSprecher2() {
        return isBlank(sprecher2) ? sprecher2 : "";
    }

    public String getSprecher() {
        final StringBuilder builder = new StringBuilder();
        if (!isBlank(sprecher1)) {
            builder.append(sprecher1);
        }
        if (!isBlank(sprecher2)) {
            if (!isBlank(sprecher1)) {
                builder.append(", ");
            }
            builder.append(sprecher2);
        }
        return builder.toString();
    }

    public String getSpieldauer() {
        String _spieldauer = "0,00";
        final boolean spieldauerParsbar = !isBlank(spieldauer)
                && (spieldauer.contains(",") || spieldauer.contains("."));
        if (spieldauerParsbar) {
            String[] parts = spieldauer.split("[.,]");
            if ("00".equals(parts[1])) {
                _spieldauer = String.format("%s Stunden", parts[0]);
            } else {
                _spieldauer = String.format("%s Stunden %s Minuten", parts[0], parts[1]);
            }
        }
        if ("0,00".equalsIgnoreCase(spieldauer)) {
            LOGGER.warn("Spieldauer ({}) von Hörbuch {} unbekannt", spieldauer, titelnummer);
        }
        return _spieldauer;
    }

    public String getProdOrt() {
        return !isBlank(prodOrt) ? prodOrt : "";
    }

    public String getProdJahr() {
        return !isBlank(prodJahr) ? prodJahr : "";
    }

    public String getSuchwoerter() {
        return !isBlank(suchwoerter) ? suchwoerter : "";
    }

    public String getAnzahlCD() {
        return !isBlank(anzahlCD) ? anzahlCD : "";
    }

    public String getTitelfamilie() {
        return !isBlank(titelfamilie) ? titelfamilie : "";
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

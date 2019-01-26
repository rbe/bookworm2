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

    public static final Hoerbuch UNBEKANNT = new Hoerbuch(Sachgebiet.UNBEKANNT,
            new Titelnummer("000000"),
            "unbekannt",
            "unbekannt", "unbekannt",
            "unbekannt",
            "unbekannt", "unbekannt", "unbekannt",
            "unbekannt", "unbekannt", "0,00",
            "unbekannt", "unbekannt",
            "unbekannt",
            "0",
            "unbekannt",
            "", null, false);

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
        einstelldatum(einstelldatum);
        this.aghNummer = aghNummer;
        this.downloadbar = downloadbar;
    }

    public Sachgebiet getSachgebiet() {
        return sachgebiet;
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
        return autor;
    }

    public void setAutor(String autor) {
        this.autor = autor;
    }

    public String getTitel() {
        return titel;
    }

    public void setTitel(String titel) {
        this.titel = titel;
    }

    public String getUntertitel() {
        return untertitel;
    }

/*
    public void setUntertitel(String untertitel) {
        this.untertitel = untertitel;
    }
*/

    public String getErlaeuterung() {
        return erlaeuterung;
    }

/*
    public void setErlaeuterung(String erlaeuterung) {
        this.erlaeuterung = erlaeuterung;
    }
*/

    public String getVerlagsort() {
        return verlagsort;
    }

/*
    public void setVerlagsort(String verlagsort) {
        this.verlagsort = verlagsort;
    }
*/

    public String getVerlag() {
        return verlag;
    }

/*
    public void setVerlag(String verlag) {
        this.verlag = verlag;
    }
*/

    public String getDruckjahr() {
        return druckjahr;
    }

/*
    public void setDruckjahr(String druckjahr) {
        this.druckjahr = druckjahr;
    }
*/

    public String getSprecher1() {
        return sprecher1;
    }

/*
    public void setSprecher1(String sprecher1) {
        this.sprecher1 = sprecher1;
    }
*/

    public String getSprecher2() {
        return sprecher2;
    }

/*
    public void setSprecher2(String sprecher2) {
        this.sprecher2 = sprecher2;
    }
*/

    public String getSprecher() {
        final StringBuilder builder = new StringBuilder();
        final boolean sprecher1HatWert = null != sprecher1 && !sprecher1.trim().isEmpty();
        if (sprecher1HatWert) {
            builder.append(sprecher1);
        }
        final boolean sprecher2HatWert = null != sprecher2 && !sprecher2.trim().isEmpty();
        if (sprecher2HatWert) {
            if (sprecher1HatWert) {
                builder.append(", ");
            }
            builder.append(sprecher2);
        }
        return builder.toString();
    }

    public String getSpieldauer() {
        String _spieldauer = "unbekannt";
        if (null != spieldauer
                && (spieldauer.contains(",") || spieldauer.contains("."))) {
            String[] parts = spieldauer.split("[.,]");
            if (!"00".equals(parts[1])) {
                _spieldauer = String.format("%s Stunden %s Minuten", parts[0], parts[1]);
            } else {
                _spieldauer = String.format("%s Stunden", parts[0]);
            }
        }
        if ("unbekannt".equalsIgnoreCase(spieldauer)) {
            LOGGER.warn("Spieldauer ({}) von Hörbuch {} unbekannt", spieldauer, titelnummer);
        }
        return _spieldauer;
    }

/*
    public void setSpieldauer(String spieldauer) {
        this.spieldauer = spieldauer;
    }
*/

    public String getProdOrt() {
        return prodOrt;
    }

/*
    public void setProdOrt(String prodOrt) {
        this.prodOrt = prodOrt;
    }
*/

    public String getProdJahr() {
        return prodJahr;
    }

/*
    public void setProdJahr(String prodJahr) {
        this.prodJahr = prodJahr;
    }
*/

    public String getSuchwoerter() {
        return suchwoerter;
    }

/*
    public void setSuchwoerter(String suchwoerter) {
        this.suchwoerter = suchwoerter;
    }
*/

    public String getAnzahlCD() {
        return anzahlCD;
    }

/*
    public void setAnzahlCD(String anzahlCD) {
        this.anzahlCD = anzahlCD;
    }
*/

    public String getTitelfamilie() {
        return titelfamilie;
    }

/*
    public void setTitelfamilie(String titelfamilie) {
        this.titelfamilie = titelfamilie;
    }
*/

    public LocalDate getEinstelldatum() {
        return einstelldatum;
    }

    public String getEinstelldatumAufDeutsch() {
        return einstelldatum.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
    }

/*
    public void setEinstelldatum(LocalDate einstelldatum) {
        this.einstelldatum = einstelldatum;
    }
*/

    private void einstelldatum(final String einstelldatum) {
        try {
            this.einstelldatum = LocalDate.parse(einstelldatum, DateTimeFormatter.BASIC_ISO_DATE);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException(einstelldatum, e);
        }
    }

    public AghNummer getAghNummer() {
        return aghNummer;
    }

    public boolean hatAghNummer(final AghNummer aghNummer) {
        Objects.requireNonNull(aghNummer);
        return null != this.aghNummer && this.aghNummer.equals(aghNummer);
    }

/*
    public void setAghNummer(final AghNummer aghNummer) {
        this.aghNummer = aghNummer;
    }

    public void setAghNummer(final String aghNummer) {
        this.aghNummer = new AghNummer(aghNummer);
    }
*/

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
        return String.format("Hoerbuch{" +
                        "titelnummer='%s'," +
                        " aghNummer='%s'," +
                        " sachgebiet='%s'," +
                        " autor='%s'," +
                        " titel='%s'" +
                        " untertitel='%s'" +
                        " erlaeuterung='%s'" +
                        " suchwoerter='%s'" +
                        "}",
                titelnummer, aghNummer, sachgebiet, autor, titel, untertitel, erlaeuterung, suchwoerter);
    }

}

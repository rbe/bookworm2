/*
 * Copyright (C) 2011-2018 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchkatalog.domain.hoerer;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonIgnore;

import wbh.bookworm.shared.domain.Hoerernummer;

import aoc.mikrokosmos.ddd.model.DomainAggregate;

public final class Hoerer extends DomainAggregate<Hoerer, Hoerernummer> {

    private static final String DD_MM_YYYY = "dd.MM.yyyy";

    /* HOERSTP, HOEAN */private String anrede;

    /* HOERSTP, HOEVN private String vorname; */
    /* HOERSTP, HOENN private String nachname; */
    /* HOERSTP */private Hoerername hoerername;

    /* HOERSTP, HOEN2 */private String nachnamenszusatz;

    /* HOERSTP, HOESTR */private String strasse;

    /* HOERSTP, HOEPLZ */private String plz;

    /* HOERSTP, HOEORT */private String ort;

    /* HOERSTP, HOENPB */private String adresszusatz;

    /* HOEKZ, HOELAN */private String land;

    /* HOERSTP, HOETV */private LocalDate sperrTerminVon;

    /* HOERSTP, HOETB */private LocalDate sperrTerminBis;

    /* HOERSTP, HOEUV */private LocalDate urlaubVon;

    /* HOERSTP, HOEUB */private LocalDate urlaubBis;

    /* HOERSTP, HOUN2 (Namenszusatz) */private String urlaubName2;

    /* HOERSTP, HOUSTR */private String urlaubStrasse;

    /* HOERSTP, HOUPLZ */private String urlaubPlz;

    /* HOERSTP, HOUORT */private String urlaubOrt;

    /* HOERSTP, HOUNPB */private String urlaubAdresszusatz;

    /* HOEKZ, HOELA2 */private String urlaubLand;

    /* HOERSTP, HOEGBD */private LocalDate geburtsdatum;

    /* HOERSTP, HOETEL */private String telefonnummer;

    /* HOEKZ, HOKZ12 */private HoererEmail hoereremail;

    /* HOEBSTP, BUMGI */private Integer mengenindex;

    /* HOEBSTP, BURDAT */private LocalDate rueckbuchungsdatum;

    public Hoerer(final Hoerernummer hoerernummer,
                  final Hoerername hoerername, final HoererEmail hoereremail) {
        super(hoerernummer);
        Objects.requireNonNull(hoerername);
        this.hoerername = hoerername;
        Objects.requireNonNull(hoereremail);
        this.hoereremail = hoereremail;
    }

    public static final Hoerer UNBEKANNT = new Hoerer(Hoerernummer.UNBEKANNT, Hoerername.UNBEKANNT, HoererEmail.UNBEKANNT);

    @SuppressWarnings({"squid:S00107"})
    public Hoerer(final Hoerernummer hoerernummer,
                  final String anrede, final Hoerername hoerername, final String nachnamenszusatz,
                  final String strasse, final String plz, final String ort, final String adresszusatz, final String land,
                  final LocalDate sperrTerminVon, final LocalDate sperrTerminBis,
                  final LocalDate urlaubVon, final LocalDate urlaubBis,
                  final String urlaubName2, final String urlaubStrasse, final String urlaubPlz, final String urlaubOrt,
                  final String urlaubAdresszusatz, final String urlaubLand,
                  final LocalDate geburtsdatum,
                  final String telefonnummer, final HoererEmail hoereremail,
                  final Integer mengenindex,
                  final LocalDate rueckbuchungsdatum) {
        super(hoerernummer);
        this.anrede = anrede;
        this.hoerername = hoerername;
        this.nachnamenszusatz = nachnamenszusatz;
        this.strasse = strasse;
        this.plz = plz;
        this.ort = ort;
        this.adresszusatz = adresszusatz;
        this.land = land;
        this.sperrTerminVon = sperrTerminVon;
        this.sperrTerminBis = sperrTerminBis;
        this.urlaubVon = urlaubVon;
        this.urlaubBis = urlaubBis;
        this.urlaubName2 = urlaubName2;
        this.urlaubStrasse = urlaubStrasse;
        this.urlaubPlz = urlaubPlz;
        this.urlaubOrt = urlaubOrt;
        this.urlaubAdresszusatz = urlaubAdresszusatz;
        this.urlaubLand = urlaubLand;
        this.geburtsdatum = geburtsdatum;
        this.telefonnummer = telefonnummer;
        this.hoereremail = hoereremail;
        this.mengenindex = mengenindex;
        this.rueckbuchungsdatum = rueckbuchungsdatum;
    }

    @JsonIgnore
    public boolean isUnbekannt() {
        return domainId.isUnbekannt();
    }

    @JsonIgnore
    public boolean isBekannt() {
        return domainId.isBekannt();
    }

    public Hoerernummer getHoerernummer() {
        return domainId;
    }

    public boolean hasHoerername() {
        return hoerername.irgendeinNameVorhanden();
    }

    public Hoerername getHoerername() {
        return hoerername;
    }

    public boolean hasVorname() {
        return null != hoerername.getVorname();
    }

    public Vorname getVorname() {
        return hoerername.getVorname();
    }

    public boolean hasNachname() {
        return null != hoerername.getNachname();
    }

    public Nachname getNachname() {
        return hoerername.getNachname();
    }

    public String getName() {
        return String.format("%s %s",
                hasVorname() ? getVorname() : "Unbekannt",
                hasNachname() ? getNachname() : "Unbekannt");
    }

    public boolean hasHoereremail() {
        return null != hoereremail;
    }

    public HoererEmail getHoereremail() {
        return hoereremail;
    }

    public String getAnrede() {
        return nonNullString(anrede);
    }

    public String getNachnamenszusatz() {
        return nonNullString(nachnamenszusatz);
    }

    public String getStrasse() {
        return nonNullString(strasse);
    }

    public String getPlz() {
        return nonNullString(plz);
    }

    public String getOrt() {
        return nonNullString(ort);
    }

    public String getAdresszusatz() {
        return nonNullString(adresszusatz);
    }

    public String getLand() {
        return nonNullString(land);
    }

    public boolean hasSperrtermin() {
        return null != sperrTerminVon || null != sperrTerminBis;
    }

    public LocalDate getSperrTerminVon() {
        return sperrTerminVon;
    }

    public String getSperrterminVonAufDeutsch() {
        return null != sperrTerminVon
                ? sperrTerminVon.format(DateTimeFormatter.ofPattern(DD_MM_YYYY))
                : "";
    }

    public LocalDate getSperrTerminBis() {
        return sperrTerminBis;
    }

    public String getSperrterminBisAufDeutsch() {
        return null != sperrTerminBis
                ? sperrTerminBis.format(DateTimeFormatter.ofPattern(DD_MM_YYYY))
                : "";
    }

    public boolean hasUrlaub() {
        return null != urlaubVon || null != urlaubBis;
    }

    public LocalDate getUrlaubVon() {
        return urlaubVon;
    }

    public String getUrlaubVonAufDeutsch() {
        return null != urlaubVon
                ? urlaubVon.format(DateTimeFormatter.ofPattern(DD_MM_YYYY))
                : "";
    }

    public LocalDate getUrlaubBis() {
        return urlaubBis;
    }

    public String getUrlaubBisAufDeutsch() {
        return null != urlaubBis
                ? urlaubBis.format(DateTimeFormatter.ofPattern(DD_MM_YYYY))
                : "";
    }

    public String getUrlaubName2() {
        return nonNullString(urlaubName2);
    }

    public String getUrlaubStrasse() {
        return nonNullString(urlaubStrasse);
    }

    public String getUrlaubPlz() {
        return nonNullString(urlaubPlz);
    }

    public String getUrlaubOrt() {
        return nonNullString(urlaubOrt);
    }

    public String getUrlaubAdresszusatz() {
        return nonNullString(urlaubAdresszusatz);
    }

    public String getUrlaubLand() {
        return nonNullString(urlaubLand);
    }

    public LocalDate getGeburtsdatum() {
        return geburtsdatum;
    }

    public String getGeburtsdatumAufDeutsch() {
        return null != geburtsdatum
                ? geburtsdatum.format(DateTimeFormatter.ofPattern(DD_MM_YYYY))
                : "";
    }

    public String getTelefonnummer() {
        return nonNullString(telefonnummer);
    }

    public Integer getMengenindex() {
        return null != mengenindex ? mengenindex : 0;
    }

    public LocalDate getRueckbuchungsdatum() {
        return rueckbuchungsdatum;
    }

    public String getRueckbuchungsdatumAufDeutsch() {
        return null != rueckbuchungsdatum
                ? rueckbuchungsdatum.format(DateTimeFormatter.ofPattern(DD_MM_YYYY))
                : "";
    }

    @Override
    public int hashCode() {
        return Objects.hash(domainId);
    }

    @Override
    public boolean equals(final Object other) {
        if (this == other) return true;
        if (other == null || getClass() != other.getClass()) return false;
        final Hoerer that = (Hoerer) other;
        return Objects.equals(that.domainId, this.domainId);
    }

    @Override
    public int compareTo(final Hoerer other) {
        return other.domainId.compareTo(this.domainId.getValue());
    }

    @Override
    public String toString() {
        return String.format("Hoerer{hoerernummer=%s}", domainId);
    }

}

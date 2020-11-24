package wbh.bookworm.hoerbuchkatalog.webservice.rest;

import java.io.Serializable;
import java.time.LocalDate;

public final class HoererAntwortDTO implements Serializable {

    private String mandant;

    private String hoerernummer;

    private String anrede;

    private String vorname;

    private String nachname;

    private String nachnamenszusatz;

    private String strasse;

    private String plz;

    private String ort;

    private String adresszusatz;

    private String land;

    private LocalDate sperrTerminVon;

    private LocalDate sperrTerminBis;

    private LocalDate urlaubVon;

    private LocalDate urlaubBis;

    private String urlaubName2;

    private String urlaubStrasse;

    private String urlaubPlz;

    private String urlaubOrt;

    private String urlaubAdresszusatz;

    private String urlaubLand;

    private LocalDate geburtsdatum;

    private String telefonnummer;

    private String hoereremail;

    private Integer mengenindex;

    private LocalDate rueckbuchungsdatum;

    public String getMandant() {
        return mandant;
    }

    public void setMandant(final String mandant) {
        this.mandant = mandant;
    }

    public String getHoerernummer() {
        return hoerernummer;
    }

    public void setHoerernummer(final String hoerernummer) {
        this.hoerernummer = hoerernummer;
    }

    public String getAnrede() {
        return anrede;
    }

    public void setAnrede(final String anrede) {
        this.anrede = anrede;
    }

    public String getVorname() {
        return vorname;
    }

    public void setVorname(final String vorname) {
        this.vorname = vorname;
    }

    public String getNachname() {
        return nachname;
    }

    public void setNachname(final String nachname) {
        this.nachname = nachname;
    }

    public String getNachnamenszusatz() {
        return nachnamenszusatz;
    }

    public void setNachnamenszusatz(final String nachnamenszusatz) {
        this.nachnamenszusatz = nachnamenszusatz;
    }

    public String getStrasse() {
        return strasse;
    }

    public void setStrasse(final String strasse) {
        this.strasse = strasse;
    }

    public String getPlz() {
        return plz;
    }

    public void setPlz(final String plz) {
        this.plz = plz;
    }

    public String getOrt() {
        return ort;
    }

    public void setOrt(final String ort) {
        this.ort = ort;
    }

    public String getAdresszusatz() {
        return adresszusatz;
    }

    public void setAdresszusatz(final String adresszusatz) {
        this.adresszusatz = adresszusatz;
    }

    public String getLand() {
        return land;
    }

    public void setLand(final String land) {
        this.land = land;
    }

    public LocalDate getSperrTerminVon() {
        return sperrTerminVon;
    }

    public void setSperrTerminVon(final LocalDate sperrTerminVon) {
        this.sperrTerminVon = sperrTerminVon;
    }

    public LocalDate getSperrTerminBis() {
        return sperrTerminBis;
    }

    public void setSperrTerminBis(final LocalDate sperrTerminBis) {
        this.sperrTerminBis = sperrTerminBis;
    }

    public LocalDate getUrlaubVon() {
        return urlaubVon;
    }

    public void setUrlaubVon(final LocalDate urlaubVon) {
        this.urlaubVon = urlaubVon;
    }

    public LocalDate getUrlaubBis() {
        return urlaubBis;
    }

    public void setUrlaubBis(final LocalDate urlaubBis) {
        this.urlaubBis = urlaubBis;
    }

    public String getUrlaubName2() {
        return urlaubName2;
    }

    public void setUrlaubName2(final String urlaubName2) {
        this.urlaubName2 = urlaubName2;
    }

    public String getUrlaubStrasse() {
        return urlaubStrasse;
    }

    public void setUrlaubStrasse(final String urlaubStrasse) {
        this.urlaubStrasse = urlaubStrasse;
    }

    public String getUrlaubPlz() {
        return urlaubPlz;
    }

    public void setUrlaubPlz(final String urlaubPlz) {
        this.urlaubPlz = urlaubPlz;
    }

    public String getUrlaubOrt() {
        return urlaubOrt;
    }

    public void setUrlaubOrt(final String urlaubOrt) {
        this.urlaubOrt = urlaubOrt;
    }

    public String getUrlaubAdresszusatz() {
        return urlaubAdresszusatz;
    }

    public void setUrlaubAdresszusatz(final String urlaubAdresszusatz) {
        this.urlaubAdresszusatz = urlaubAdresszusatz;
    }

    public String getUrlaubLand() {
        return urlaubLand;
    }

    public void setUrlaubLand(final String urlaubLand) {
        this.urlaubLand = urlaubLand;
    }

    public LocalDate getGeburtsdatum() {
        return geburtsdatum;
    }

    public void setGeburtsdatum(final LocalDate geburtsdatum) {
        this.geburtsdatum = geburtsdatum;
    }

    public String getTelefonnummer() {
        return telefonnummer;
    }

    public void setTelefonnummer(final String telefonnummer) {
        this.telefonnummer = telefonnummer;
    }

    public String getHoereremail() {
        return hoereremail;
    }

    public void setHoereremail(final String hoereremail) {
        this.hoereremail = hoereremail;
    }

    public Integer getMengenindex() {
        return mengenindex;
    }

    public void setMengenindex(final Integer mengenindex) {
        this.mengenindex = mengenindex;
    }

    public LocalDate getRueckbuchungsdatum() {
        return rueckbuchungsdatum;
    }

    public void setRueckbuchungsdatum(final LocalDate rueckbuchungsdatum) {
        this.rueckbuchungsdatum = rueckbuchungsdatum;
    }

}

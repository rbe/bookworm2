package wbh.bookworm.hoerbuchkatalog.webservice.rest;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public final class HoerbuchAntwortDTO {

    private String titelnummer;

    private String sachgebiet;

    private String sachgebietBezeichnung;

    private String autor;

    private String titel;

    private String untertitel;

    private String erlaeuterung;

    private String verlagsort;

    private String verlag;

    private String druckjahr;

    private String sprecher1;

    private String sprecher2;

    private String spieldauer;

    private String produktionsort;

    private String produktionsjahr;

    private String suchwoerter;

    private String anzahlCD;

    private String titelfamilie;

    private LocalDate einstelldatum;

    private String aghNummer;

    private boolean downloadbar;

    private boolean alsDownloadGebucht;

    private boolean aufDerMerkliste;

    private boolean imWarenkorb;

    public String getTitelnummer() {
        return titelnummer;
    }

    public void setTitelnummer(final String titelnummer) {
        this.titelnummer = titelnummer;
    }

    public String getSachgebiet() {
        return sachgebiet;
    }

    public void setSachgebiet(final String sachgebiet) {
        this.sachgebiet = sachgebiet;
    }

    public String getSachgebietBezeichnung() {
        return sachgebietBezeichnung;
    }

    public void setSachgebietBezeichnung(final String sachgebietBezeichnung) {
        this.sachgebietBezeichnung = sachgebietBezeichnung;
    }

    public String getAutor() {
        return autor;
    }

    public void setAutor(final String autor) {
        this.autor = autor;
    }

    public String getTitel() {
        return titel;
    }

    public void setTitel(final String titel) {
        this.titel = titel;
    }

    public String getUntertitel() {
        return untertitel;
    }

    public void setUntertitel(final String untertitel) {
        this.untertitel = untertitel;
    }

    public String getErlaeuterung() {
        return erlaeuterung;
    }

    public void setErlaeuterung(final String erlaeuterung) {
        this.erlaeuterung = erlaeuterung;
    }

    public String getVerlagsort() {
        return verlagsort;
    }

    public void setVerlagsort(final String verlagsort) {
        this.verlagsort = verlagsort;
    }

    public String getVerlag() {
        return verlag;
    }

    public void setVerlag(final String verlag) {
        this.verlag = verlag;
    }

    public String getDruckjahr() {
        return druckjahr;
    }

    public void setDruckjahr(final String druckjahr) {
        this.druckjahr = druckjahr;
    }

    public String getSprecher1() {
        return sprecher1;
    }

    public void setSprecher1(final String sprecher1) {
        this.sprecher1 = sprecher1;
    }

    public String getSprecher2() {
        return sprecher2;
    }

    public void setSprecher2(final String sprecher2) {
        this.sprecher2 = sprecher2;
    }

    public String getSpieldauer() {
        return spieldauer;
    }

    public void setSpieldauer(final String spieldauer) {
        this.spieldauer = spieldauer;
    }

    public String getProduktionsort() {
        return produktionsort;
    }

    public void setProduktionsort(final String produktionsort) {
        this.produktionsort = produktionsort;
    }

    public String getProduktionsjahr() {
        return produktionsjahr;
    }

    public void setProduktionsjahr(final String produktionsjahr) {
        this.produktionsjahr = produktionsjahr;
    }

    public String getSuchwoerter() {
        return suchwoerter;
    }

    public void setSuchwoerter(final String suchwoerter) {
        this.suchwoerter = suchwoerter;
    }

    public String getAnzahlCD() {
        return anzahlCD;
    }

    public void setAnzahlCD(final String anzahlCD) {
        this.anzahlCD = anzahlCD;
    }

    public String getTitelfamilie() {
        return titelfamilie;
    }

    public void setTitelfamilie(final String titelfamilie) {
        this.titelfamilie = titelfamilie;
    }

    public LocalDate getEinstelldatum() {
        return einstelldatum;
    }

    public void setEinstelldatum(final LocalDate einstelldatum) {
        this.einstelldatum = einstelldatum;
    }

    public String getEinstelldatumAufDeutsch() {
        return null != einstelldatum
                ? einstelldatum.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"))
                : "";
    }

    public String getAghNummer() {
        return aghNummer;
    }

    public void setAghNummer(final String aghNummer) {
        this.aghNummer = aghNummer;
    }

    public boolean isDownloadbar() {
        // TODO return downloadbar;
        return null != aghNummer && !aghNummer.isBlank();
    }

    public void setDownloadbar(final boolean downloadbar) {
        this.downloadbar = downloadbar;
    }

    public boolean isAlsDownloadGebucht() {
        return alsDownloadGebucht;
    }

    public void setAlsDownloadGebucht(final boolean alsDownloadGebucht) {
        this.alsDownloadGebucht = alsDownloadGebucht;
    }

    public boolean isAufDerMerkliste() {
        return aufDerMerkliste;
    }

    public void setAufDerMerkliste(final boolean aufDerMerkliste) {
        this.aufDerMerkliste = aufDerMerkliste;
    }

    public boolean isImWarenkorb() {
        return imWarenkorb;
    }

    public void setImWarenkorb(final boolean imWarenkorb) {
        this.imWarenkorb = imWarenkorb;
    }

}

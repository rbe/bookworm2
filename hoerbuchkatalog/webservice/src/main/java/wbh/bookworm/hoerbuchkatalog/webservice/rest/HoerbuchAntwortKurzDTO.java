package wbh.bookworm.hoerbuchkatalog.webservice.rest;

public final class HoerbuchAntwortKurzDTO {

    private String titelnummer;

    private String sachgebiet;

    private String sachgebietBezeichnung;

    private String autor;

    private String titel;

    private String aghNummer;

    private boolean downloadbar;

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

    public String getAghNummer() {
        return aghNummer;
    }

    public void setAghNummer(final String aghNummer) {
        this.aghNummer = aghNummer;
    }

    public boolean isDownloadbar() {
        return downloadbar;
    }

    public void setDownloadbar(final boolean downloadbar) {
        this.downloadbar = downloadbar;
    }

}
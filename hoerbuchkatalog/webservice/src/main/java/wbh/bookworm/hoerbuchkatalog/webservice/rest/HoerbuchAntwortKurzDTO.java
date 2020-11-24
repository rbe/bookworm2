package wbh.bookworm.hoerbuchkatalog.webservice.rest;

public final class HoerbuchAntwortKurzDTO {

    private String titelnummer;

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

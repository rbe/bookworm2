package wbh.bookworm.hoerbuchkatalog.webservice.katalog;

public final class HoerbuchInfo implements Comparable<HoerbuchInfo> {

    private String titelnummer;

    private String sachgebiet;

    private String sachgebietBezeichnung;

    private String autor;

    private String titel;

    private String aghNummer;

    private boolean downloadbar;

    private boolean downloadErlaubt;

    private boolean alsDownloadGebucht;

    private int anzahlDownloads;

    private String ausgeliehenAm;

    private String rueckgabeBis;

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

    public boolean isDownloadErlaubt() {
        return downloadErlaubt;
    }

    public void setDownloadErlaubt(final boolean downloadErlaubt) {
        this.downloadErlaubt = downloadErlaubt;
    }

    public boolean isAlsDownloadGebucht() {
        return alsDownloadGebucht;
    }

    public void setAlsDownloadGebucht(final boolean alsDownloadGebucht) {
        this.alsDownloadGebucht = alsDownloadGebucht;
    }

    public int getAnzahlDownloads() {
        return anzahlDownloads;
    }

    public void setAnzahlDownloads(final int anzahlDownloads) {
        this.anzahlDownloads = anzahlDownloads;
    }

    public String getAusgeliehenAm() {
        return null != ausgeliehenAm && !ausgeliehenAm.isBlank()
                ? ausgeliehenAm
                : "";
    }

    public void setAusgeliehenAm(final String ausgeliehenAm) {
        this.ausgeliehenAm = ausgeliehenAm;
    }

    public String getRueckgabeBis() {
        return null != rueckgabeBis && !rueckgabeBis.isBlank() ? rueckgabeBis : "";
    }

    public void setRueckgabeBis(final String rueckgabeBis) {
        this.rueckgabeBis = rueckgabeBis;
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

    @Override
    public int compareTo(final HoerbuchInfo o) {
        return this.titelnummer.compareTo(o.titelnummer);
    }

}

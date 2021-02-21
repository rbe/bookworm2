package wbh.bookworm.hoerbuchkatalog.webservice.admin;

import java.io.Serializable;

import wbh.bookworm.shared.domain.Hoerernummer;

public class TemplateModel implements Serializable {

    private Hoerernummer hoerernummer;

    private Integer anzahlBestellungenProAusleihzeitraum;

    private Integer anzahlBestellungenProTag;

    private Integer anzahlDownloadsProHoerbuch;

    public TemplateModel() {
    }

    public TemplateModel(final Kontingent kontingent) {
        this.hoerernummer = kontingent.getHoerernummer();
        this.anzahlBestellungenProAusleihzeitraum = kontingent.getAnzahlBestellungenProAusleihzeitraum();
        this.anzahlBestellungenProTag = kontingent.getAnzahlBestellungenProTag();
        this.anzahlDownloadsProHoerbuch = kontingent.getAnzahlDownloadsProHoerbuch();
    }

    public Hoerernummer getHoerernummer() {
        return hoerernummer;
    }

    public void setHoerernummer(final Hoerernummer hoerernummer) {
        this.hoerernummer = hoerernummer;
    }

    public Integer getAnzahlBestellungenProAusleihzeitraum() {
        return anzahlBestellungenProAusleihzeitraum;
    }

    public void setAnzahlBestellungenProAusleihzeitraum(final Integer anzahlBestellungenProAusleihzeitraum) {
        this.anzahlBestellungenProAusleihzeitraum = anzahlBestellungenProAusleihzeitraum;
    }

    public Integer getAnzahlBestellungenProTag() {
        return anzahlBestellungenProTag;
    }

    public void setAnzahlBestellungenProTag(final Integer anzahlBestellungenProTag) {
        this.anzahlBestellungenProTag = anzahlBestellungenProTag;
    }

    public Integer getAnzahlDownloadsProHoerbuch() {
        return anzahlDownloadsProHoerbuch;
    }

    public void setAnzahlDownloadsProHoerbuch(final Integer anzahlDownloadsProHoerbuch) {
        this.anzahlDownloadsProHoerbuch = anzahlDownloadsProHoerbuch;
    }

}

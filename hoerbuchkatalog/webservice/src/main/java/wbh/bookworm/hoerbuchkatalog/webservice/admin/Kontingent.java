package wbh.bookworm.hoerbuchkatalog.webservice.admin;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.io.Serializable;
import java.util.Optional;

import wbh.bookworm.shared.domain.Hoerernummer;

public class Kontingent implements Serializable {

    private Hoerernummer hoerernummer;

    @Min(value = 0, message = "")
    @Max(value = 100, message = "")
    private Integer anzahlBestellungenProAusleihzeitraum;

    @Min(value = 0, message = "")
    @Max(value = 30, message = "")
    private Integer anzahlBestellungenProTag;

    @Min(value = 0, message = "")
    @Max(value = 10, message = "")
    private Integer anzahlDownloadsProHoerbuch;

    public Hoerernummer getHoerernummer() {
        return Optional.ofNullable(hoerernummer).orElse(Hoerernummer.UNBEKANNT);
    }

    public void setHoerernummer(final Hoerernummer hoerernummer) {
        this.hoerernummer = hoerernummer;
    }

    public Integer getAnzahlBestellungenProAusleihzeitraum() {
        return Optional.ofNullable(anzahlBestellungenProAusleihzeitraum).orElse(-1);
    }

    public void setAnzahlBestellungenProAusleihzeitraum(final Integer anzahlBestellungenProAusleihzeitraum) {
        this.anzahlBestellungenProAusleihzeitraum = anzahlBestellungenProAusleihzeitraum;
    }

    public Integer getAnzahlBestellungenProTag() {
        return Optional.ofNullable(anzahlBestellungenProTag).orElse(-1);
    }

    public void setAnzahlBestellungenProTag(final Integer anzahlBestellungenProTag) {
        this.anzahlBestellungenProTag = anzahlBestellungenProTag;
    }

    public Integer getAnzahlDownloadsProHoerbuch() {
        return Optional.ofNullable(anzahlDownloadsProHoerbuch).orElse(-1);
    }

    public void setAnzahlDownloadsProHoerbuch(final Integer anzahlDownloadsProHoerbuch) {
        this.anzahlDownloadsProHoerbuch = anzahlDownloadsProHoerbuch;
    }

}

package wbh.bookworm.hoerbuchkatalog.webservice.rest;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class HoerbuchAntwortDTO {

    private final String titelnummer;

    private final String titel;

    private final String autor;

    @JsonCreator
    public HoerbuchAntwortDTO(@JsonProperty("titelnummer") final String titelnummer,
                              @JsonProperty("titel") final String titel,
                              @JsonProperty("autor") final String autor) {
        this.titelnummer = titelnummer;
        this.titel = titel;
        this.autor = autor;
    }

    public String getTitelnummer() {
        return titelnummer;
    }

    public String getTitel() {
        return titel;
    }

    public String getAutor() {
        return autor;
    }

}

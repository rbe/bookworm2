package wbh.bookworm.hoerbuchkatalog.webservice.rest;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public final class HoerbuchAnfrageDTO implements Serializable {

    private final String titelnummer;

    @JsonCreator
    public HoerbuchAnfrageDTO(@JsonProperty("titelnummer") final String titelnummer) {
        this.titelnummer = titelnummer;
    }

    public String getTitelnummer() {
        return titelnummer;
    }

    @Override
    public String toString() {
        return String.format("HoerbuchDTO{titelnummer='%s'}", titelnummer);
    }

}

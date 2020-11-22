package wbh.bookworm.hoerbuchkatalog.webservice.rest;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class HoererAnfrageDTO implements Serializable {

    private final String mandant;

    private final String hoerernummer;

    @JsonCreator
    public HoererAnfrageDTO(@JsonProperty("mandant") final String mandant,
                            @JsonProperty("hoerernummer") final String hoerernummer) {
        this.mandant = mandant;
        this.hoerernummer = hoerernummer;
    }

    public String getMandant() {
        return mandant;
    }

    public String getHoerernummer() {
        return hoerernummer;
    }

}

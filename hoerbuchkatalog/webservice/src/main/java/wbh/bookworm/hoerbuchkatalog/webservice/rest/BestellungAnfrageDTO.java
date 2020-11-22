package wbh.bookworm.hoerbuchkatalog.webservice.rest;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class BestellungAnfrageDTO implements Serializable {

    private final String hoerername;

    private final String hoereremail;

    private final String bemerkung;

    private final Boolean bestellkarteMischen;

    private final Boolean alteBestellkarteLoeschen;

    @JsonCreator
    public BestellungAnfrageDTO(@JsonProperty("hoerername") final String hoerername,
                                @JsonProperty("hoereremail") final String hoereremail,
                                @JsonProperty("bemerkung") final String bemerkung,
                                @JsonProperty("bestellkarteMischen") final Boolean bestellkarteMischen,
                                @JsonProperty("alteBestellkarteLoeschen") final Boolean alteBestellkarteLoeschen) {
        this.hoerername = hoerername;
        this.hoereremail = hoereremail;
        this.bemerkung = bemerkung;
        this.bestellkarteMischen = bestellkarteMischen;
        this.alteBestellkarteLoeschen = alteBestellkarteLoeschen;
    }

    public String getHoerername() {
        return hoerername;
    }

    public String getHoereremail() {
        return hoereremail;
    }

    public String getBemerkung() {
        return bemerkung;
    }

    public Boolean getBestellkarteMischen() {
        return bestellkarteMischen;
    }

    public Boolean getAlteBestellkarteLoeschen() {
        return alteBestellkarteLoeschen;
    }

    @Override
    public String toString() {
        return String.format("BestellungDTO{hoerername='%s', hoereremail='%s'}", hoerername, hoereremail);
    }

}

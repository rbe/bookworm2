package wbh.bookworm.hoerbuchdienst.adapter.provided.stream;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.micronaut.core.annotation.Introspected;

@Introspected
public final class HoerbuchAnfrageDTO implements Serializable {

    private static final long serialVersionUID = 4374913262529116624L;

    @Min(3)
    private final String mandant;

    @Min(2)
    private final String hoerernummer;

    @Min(13)
    @Max(13)
    private final String aghNummer;

    @Min(1)
    @Max(5)
    private final String titelnummer;

    @JsonCreator
    public HoerbuchAnfrageDTO(@JsonProperty("mandant") final String mandant,
                              @JsonProperty("hoerernummer") final String hoerernummer,
                              @JsonProperty("aghNummer") final String aghNummer,
                              @JsonProperty("titelnummer") final String titelnummer) {
        this.mandant = mandant;
        this.hoerernummer = hoerernummer;
        this.aghNummer = aghNummer;
        this.titelnummer = titelnummer;
    }

    public String getMandant() {
        return mandant;
    }

    public String getHoerernummer() {
        return hoerernummer;
    }

    public String getAghNummer() {
        return aghNummer;
    }

    public String getTitelnummer() {
        return titelnummer;
    }

    @Override
    public String toString() {
        return String.format("HoerbuchAnfrageDTO{mandant='%s', hoerernummer='%s', aghNummer='%s', titelnummer='%s'}",
                mandant, hoerernummer, aghNummer, titelnummer);
    }

}

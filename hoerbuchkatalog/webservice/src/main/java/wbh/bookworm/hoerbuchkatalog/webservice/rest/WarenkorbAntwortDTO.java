package wbh.bookworm.hoerbuchkatalog.webservice.rest;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class WarenkorbAntwortDTO implements Serializable {

    private final List<HoerbuchAntwortDTO> hoerbucher;

    @JsonCreator
    public WarenkorbAntwortDTO(@JsonProperty("hoerbucher") final List<HoerbuchAntwortDTO> hoerbucher) {
        this.hoerbucher = hoerbucher;
    }

    public List<HoerbuchAntwortDTO> getHoerbucher() {
        return hoerbucher;
    }

}

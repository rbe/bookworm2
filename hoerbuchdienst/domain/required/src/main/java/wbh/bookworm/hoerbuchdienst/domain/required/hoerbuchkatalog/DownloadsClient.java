package wbh.bookworm.hoerbuchdienst.domain.required.hoerbuchkatalog;

import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Header;
import io.micronaut.http.annotation.Put;

//@Client("http://hoerbuchkatalog.wbh-online.de/v1/downloads")
@Header(name = "Origin", value = "https://www.wbh-online.de")
@Header(name = "X-Bookworm-Mandant", value = "06")
public interface DownloadsClient {

    @Get
    String downloads();

    @Put(value = "/{titelnummer}", single = true)
    void verbuche(@Header("X-Bookworm-Hoerernummer") String hoerernummer, String titelnummer, @Body String body);

}

package wbh.bookworm.hoerbuchdienst.adapter.required.hoerbuchkatalog;

import javax.inject.Inject;
import java.net.MalformedURLException;

import io.micronaut.http.client.RxHttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import org.junit.jupiter.api.Test;

import wbh.bookworm.hoerbuchdienst.domain.required.hoerbuchkatalog.DownloadsClient;

@MicronautTest
class DownloadsClientTest {

    @Inject
    HoerbuchkatalogClientImpl hoerbuchkatalogClient;

    @Test
    void verbuche() {
        hoerbuchkatalogClient.verbuche("80170", "21052");
    }

}

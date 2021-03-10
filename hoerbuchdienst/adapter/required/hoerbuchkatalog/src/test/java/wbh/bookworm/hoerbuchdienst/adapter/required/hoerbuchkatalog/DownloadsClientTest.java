package wbh.bookworm.hoerbuchdienst.adapter.required.hoerbuchkatalog;

import javax.inject.Inject;

import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

@MicronautTest
@Disabled("Hörbuchkatalog muss erreichbar sein")
class DownloadsClientTest {

    @Inject
    HoerbuchkatalogClientImpl hoerbuchkatalogClient;

    @Test
    void verbuche() {
        hoerbuchkatalogClient.verbuche("80170", "21052");
    }

}

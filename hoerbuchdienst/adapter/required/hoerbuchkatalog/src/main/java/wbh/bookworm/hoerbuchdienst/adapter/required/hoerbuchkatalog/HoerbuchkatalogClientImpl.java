package wbh.bookworm.hoerbuchdienst.adapter.required.hoerbuchkatalog;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.micronaut.context.annotation.Value;
import io.micronaut.core.io.buffer.ByteBuffer;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MutableHttpRequest;
import io.micronaut.http.client.RxHttpClient;
import io.micronaut.http.client.annotation.Client;
import io.reactivex.Flowable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import wbh.bookworm.hoerbuchdienst.domain.required.hoerbuchkatalog.HoerbuchkatalogClient;

@Singleton
class HoerbuchkatalogClientImpl implements HoerbuchkatalogClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(HoerbuchkatalogClientImpl.class);

    private final RxHttpClient httpClient;

    @Inject
    HoerbuchkatalogClientImpl(@Value("${hoerbuchkatalog.url}") final String hoerbuchkatalogUrl,
                              @Client("${hoerbuchkatalog.url}") final RxHttpClient httpClient) {
        LOGGER.info("HTTP Client für Hörbuchkatalog {}", hoerbuchkatalogUrl);
        this.httpClient = httpClient;
    }

    public void verbuche(final String hoerernummer, final String titelnummer) {
        final MutableHttpRequest<String> request = HttpRequest.PUT("/v1/downloads/" + titelnummer, "");
        request.header("X-Bookworm-Mandant", "06");
        request.header("X-Bookworm-Hoerernummer", hoerernummer);
        request.header("Origin", "www.wbh-online.de");
        final Flowable<HttpResponse<ByteBuffer>> response = httpClient.exchange(request);
        LOGGER.info("Hörer {}: Download {} verbucht: HTTP Status {}", hoerernummer, titelnummer,
                response.blockingSingle().getStatus());
    }

}

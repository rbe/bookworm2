package wbh.bookworm.hoerbuchdienst.sharding.shared;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.net.URI;
import java.util.function.Function;
import java.util.function.Supplier;

import io.micronaut.http.HttpHeaders;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import wbh.bookworm.hoerbuchdienst.domain.ports.audiobook.AudiobookLocationService;

@Singleton
public final class AudiobookShardRedirector {

    private static final Logger LOGGER = LoggerFactory.getLogger(AudiobookShardRedirector.class);

    private static final String X_SHARD_LOCATION = "X-Shard-Location";

    private final AudiobookLocationService audiobookLocationService;

    @Inject
    AudiobookShardRedirector(final AudiobookLocationService audiobookLocationService) {
        this.audiobookLocationService = audiobookLocationService;
    }

    public <T> HttpResponse<T> withLocalOrRedirect(final /* TODO Mandantenspezifisch */String titelnummer,
                                                   final Supplier<? extends T> audiobookSupplier,
                                                   final Function<? super T, ? extends HttpResponse<T>> httpResponseSupplier,
                                                   final T emptyResponseBody,
                                                   final String serviceUri,
                                                   final HttpRequest<?> httpRequest) {
        final HttpResponse<T> result;
        final boolean locatedLocal = audiobookLocationService.isLocatedLocal(titelnummer);
        if (locatedLocal) {
            final T apply = audiobookSupplier.get();
            result = httpResponseSupplier.apply(apply);
        } else {
            result = tryRedirectToOwningShard(titelnummer, emptyResponseBody, serviceUri, httpRequest);
        }
        return result;
    }

    private <T> HttpResponse<T> tryRedirectToOwningShard(final String objectId,
                                                         final T emptyResponseBody,
                                                         final String serviceUri,
                                                         final HttpRequest<?> httpRequest) {
        final HttpResponse<T> result;
        final String shardName = audiobookLocationService.shardLocation(objectId);
        final String origin = httpRequest.getHeaders().get("Origin");
        if ("unknown".equals(shardName)) {
            result = HttpResponse.<T>notFound()
                    .header(X_SHARD_LOCATION, shardName)
                    .header(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, origin)
                    .header(HttpHeaders.ACCESS_CONTROL_ALLOW_METHODS, "GET, POST, OPTIONS")
                    .body(emptyResponseBody);
        } else {
            final String shardURI = String.format("https://%s/%s", shardName, serviceUri);
            LOGGER.info("Hörbuch '{}': Redirecting to {}", objectId, shardURI);
            result = HttpResponse.<T>temporaryRedirect(URI.create(shardURI))
                    .header(X_SHARD_LOCATION, shardName)
                    .header(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, origin)
                    .header(HttpHeaders.ACCESS_CONTROL_ALLOW_METHODS, "GET, POST, OPTIONS")
                    .body(emptyResponseBody);
        }
        return result;
    }

}

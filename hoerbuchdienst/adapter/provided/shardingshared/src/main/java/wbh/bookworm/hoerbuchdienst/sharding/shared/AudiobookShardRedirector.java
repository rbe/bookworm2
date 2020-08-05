package wbh.bookworm.hoerbuchdienst.sharding.shared;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.net.URI;
import java.util.function.Function;
import java.util.function.Supplier;

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
                                                   final String serviceUri) {
        final HttpResponse<T> result;
        final boolean locatedLocal = audiobookLocationService.isLocatedLocal(titelnummer);
        if (locatedLocal) {
            final T apply = audiobookSupplier.get();
            result = httpResponseSupplier.apply(apply);
        } else {
            result = tryRedirectToOwningShard(titelnummer, emptyResponseBody, serviceUri);
        }
        return result;
    }

    private <T> HttpResponse<T> tryRedirectToOwningShard(final String objectId,
                                                         final T emptyResponseBody,
                                                         final String serviceUri) {
        final HttpResponse<T> result;
        final String shardName = audiobookLocationService.shardLocation(objectId);
        if ("unknown".equals(shardName)) {
            result = HttpResponse.<T>notFound()
                    .header(X_SHARD_LOCATION, shardName)
                    .body(emptyResponseBody);
        } else {
            final String shardURI = String.format("https://%s/%s", shardName, serviceUri);
            LOGGER.debug("Hörbuch '{}': Redirecting to {}", objectId, shardURI);
            result = HttpResponse.<T>temporaryRedirect(URI.create(shardURI))
                    .header(X_SHARD_LOCATION, shardName)
                    .body(emptyResponseBody);
        }
        return result;
    }

}

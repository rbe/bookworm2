package wbh.bookworm.hoerbuchdienst.sharding.shared;

import java.net.URI;
import java.util.function.Function;

import io.micronaut.http.HttpHeaders;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.MutableHttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class CORS {

    private static final Logger LOGGER = LoggerFactory.getLogger(CORS.class);

    private static final String ALLOWED_DOMAIN = "*";

    static final String ALLOWED_METHODS = "OPTIONS, GET, POST";

    private CORS() {
        throw new AssertionError();
    }

    public static <T> MutableHttpResponse<T> response(final HttpRequest<?> httpRequest, final T dto) {
        return with(httpRequest,
                origin -> {
                    final MutableHttpResponse<T> response = HttpResponse.ok();
                    maybeAddOrigin(origin, response);
                    return response
                            .contentType(MediaType.APPLICATION_JSON_TYPE)
                            .body(dto);
                });
    }

    public static MutableHttpResponse<String> temporaryRedirect(final HttpRequest<?> httpRequest,
                                                                final URI uri) {
        return with(httpRequest,
                origin -> HttpResponse.<String>redirect(uri)
                        .header(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, "*"/*origin*/)
                        .header(HttpHeaders.ACCESS_CONTROL_ALLOW_METHODS, ALLOWED_METHODS)
                        .body(""));
    }

    public static MutableHttpResponse<String> optionsResponse(final HttpRequest<?> httpRequest) {
        return with(httpRequest,
                origin -> HttpResponse.<String>noContent()
                        .header(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, "*"/*origin*/)
                        .header(HttpHeaders.ACCESS_CONTROL_ALLOW_METHODS, ALLOWED_METHODS)
                        .header(HttpHeaders.ACCESS_CONTROL_ALLOW_HEADERS, "DNT,User-Agent,X-Requested-With,If-Modified-Since,Cache-Control,Origin,Accept,Content-Type,Range")
                        .header(HttpHeaders.ACCESS_CONTROL_MAX_AGE, "86400")
                        .contentType(MediaType.TEXT_PLAIN_TYPE)
                        .contentLength(0L)
                        .body(""));
    }

    private static <T> MutableHttpResponse<T> with(final HttpRequest<?> httpRequest,
                                                   final Function<? super String, ? extends MutableHttpResponse<T>> supplier) {
        httpRequest.getHeaders().forEach(entry -> LOGGER.trace("{}: {}", entry.getKey(), entry.getValue()));
        final String origin = httpRequest.getHeaders().get("Origin");
        if (null == origin || origin.endsWith(ALLOWED_DOMAIN)) {
            return supplier.apply(null != origin ? origin : "");
        } else {
            LOGGER.error("HTTP header 'Origin' == {} != {}", origin, ALLOWED_DOMAIN);
            return HttpResponse.unauthorized();
        }
    }

    private static <T> void maybeAddOrigin(final String origin, final MutableHttpResponse<T> response) {
        if (null != origin) {
            response.header(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, "*"/*origin*/)
                    .header(HttpHeaders.ACCESS_CONTROL_ALLOW_METHODS, ALLOWED_METHODS);
        }
    }

}

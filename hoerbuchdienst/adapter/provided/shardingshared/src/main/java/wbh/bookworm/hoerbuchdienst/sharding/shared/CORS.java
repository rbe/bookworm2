package wbh.bookworm.hoerbuchdienst.sharding.shared;

import io.micronaut.http.HttpHeaders;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.MutableHttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class CORS {

    private static final Logger LOGGER = LoggerFactory.getLogger(CORS.class);

    private static final String ALLOWED_DOMAIN = "audiobook.wbh-online.de";

    private CORS() {
        throw new AssertionError();
    }

    public static MutableHttpResponse<String> corsResponse(final HttpRequest<?> httpRequest) {
        httpRequest.getHeaders().forEach(entry -> LOGGER.debug("{}: {}", entry.getKey(), entry.getValue()));
        final String origin = httpRequest.getHeaders().get("Origin");
        if (null == origin) {
            LOGGER.error("Missing HTTP header 'Origin'");
            return HttpResponse.unauthorized();
        }
        if (origin.endsWith(ALLOWED_DOMAIN)) {
            return HttpResponse.<String>noContent()
                    .header(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, origin)
                    .header(HttpHeaders.ACCESS_CONTROL_ALLOW_METHODS, "GET, POST, OPTIONS")
                    .header(HttpHeaders.ACCESS_CONTROL_ALLOW_HEADERS, "DNT,User-Agent,X-Requested-With,If-Modified-Since,Cache-Control,Content-Type,Range")
                    .contentType(MediaType.TEXT_PLAIN_TYPE)
                    .contentLength(0L)
                    .body("");
        } else {
            LOGGER.error("HTTP header 'Origin' != {}", ALLOWED_DOMAIN);
            return HttpResponse.unauthorized();
        }
    }

}

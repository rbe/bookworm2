package wbh.bookworm.hoerbuchdienst.sharding.shared;

import io.micronaut.http.HttpRequest;
import io.micronaut.http.MutableHttpResponse;
import io.micronaut.http.annotation.Filter;
import io.micronaut.http.filter.HttpServerFilter;
import io.micronaut.http.filter.ServerFilterChain;
import io.micronaut.http.filter.ServerFilterPhase;
import org.reactivestreams.Publisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Filter("/**")
public class AuthenticationFilter implements HttpServerFilter {

    private static final Logger LOGGER = LoggerFactory.getLogger(AuthenticationFilter.class);

    @Override
    public Publisher<MutableHttpResponse<?>> doFilter(final HttpRequest<?> request,
                                                      final ServerFilterChain chain) {
        final String mandant = request.getHeaders().get("X-Bookworm-Mandant");
        final String hoerernummer = request.getHeaders().get("X-Bookworm-Hoerernummer");
        if (null == mandant || mandant.isBlank() || null == hoerernummer || hoerernummer.isBlank()) {
            LOGGER.debug("X-Bookworm-Mandant={} X-Bookworm-Hoerernummer={}", mandant, hoerernummer);
        }
        return chain.proceed(request);
    }

    @Override
    public int getOrder() {
        return ServerFilterPhase.SECURITY.order();
    }

}

package wbh.bookworm.hoerbuchdienst.adapter.provided.stream;

import io.micronaut.http.HttpRequest;
import io.micronaut.http.MutableHttpResponse;
import io.micronaut.http.annotation.Filter;
import io.micronaut.http.filter.HttpServerFilter;
import io.micronaut.http.filter.ServerFilterChain;
import io.micronaut.http.filter.ServerFilterPhase;
import org.reactivestreams.Publisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import wbh.bookworm.hoerbuchdienst.adapter.provided.api.BusinessException;

// TODO @Filter({"/v1/bestellung", "/v1/bestellung/**"})
public class BestellungAuthenticationFilter implements HttpServerFilter {

    private static final Logger LOGGER = LoggerFactory.getLogger(BestellungAuthenticationFilter.class);

    @Override
    public Publisher<MutableHttpResponse<?>> doFilter(final HttpRequest<?> request,
                                                      final ServerFilterChain chain) {
        final String mandant = request.getHeaders().get("X-Bookworm-Mandant");
        checkMandant(mandant);
        final String hoerernummer = request.getHeaders().get("X-Bookworm-Hoerernummer");
        checkHoerernummer(hoerernummer);
        return chain.proceed(request);
    }

    private void checkMandant(String mandant) {
        if (null == mandant || mandant.isBlank() || !"06".equals(mandant)) {
            throw new BusinessException("Ungültiger Mandant");
        }
    }

    private void checkHoerernummer(String hoerernummer) {
        if (null == hoerernummer || hoerernummer.isBlank() || !"00000".equals(hoerernummer)) {
            throw new BusinessException("Ungültige Anfrage");
        }
    }

    @Override
    public int getOrder() {
        return ServerFilterPhase.SECURITY.order();
    }

}

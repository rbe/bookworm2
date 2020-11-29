package wbh.bookworm.hoerbuchdienst.sharding.shared;

import java.util.Optional;

import io.micronaut.http.HttpMethod;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.MutableHttpResponse;
import io.micronaut.http.annotation.Filter;
import io.micronaut.http.filter.HttpServerFilter;
import io.micronaut.http.filter.ServerFilterChain;
import io.micronaut.http.filter.ServerFilterPhase;
import io.reactivex.Flowable;
import io.reactivex.schedulers.Schedulers;
import org.reactivestreams.Publisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static wbh.bookworm.hoerbuchdienst.sharding.shared.CORS.optionsResponse;

@Filter("/**")
public class CorsOptionsFilter implements HttpServerFilter {

    private static final Logger LOGGER = LoggerFactory.getLogger(CorsOptionsFilter.class);

    @Override
    public Publisher<MutableHttpResponse<?>> doFilter(final HttpRequest<?> request,
                                                      final ServerFilterChain chain) {
        if (HttpMethod.OPTIONS == request.getMethod()) {
            final MutableHttpResponse<String> stringMutableHttpResponse = optionsResponse(request);
            final Flowable<Boolean> flowable = Flowable.fromCallable(() -> {
                final Optional<String> body = request.getBody(String.class);
                if (body.isPresent()) {
                    LOGGER.debug("BODY PRESENT: {}", body.get());
                } else {
                    LOGGER.debug("BODY NOT PRESENT");
                }
                return true;
            }).subscribeOn(Schedulers.io());
            return flowable.switchMap(aBoolean -> chain.proceed(request));
        } else {
            return chain.proceed(request);
        }
    }

    @Override
    public int getOrder() {
        return ServerFilterPhase.SECURITY.order();
    }

}

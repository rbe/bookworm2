package wbh.bookworm.hoerbuchdienst.adapter.required.daisy.audiobookrepository;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.IOException;
import java.util.Optional;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.micronaut.core.convert.ArgumentConversionContext;
import io.micronaut.core.type.Argument;
import io.micronaut.rabbitmq.bind.RabbitConsumerState;
import io.micronaut.rabbitmq.bind.RabbitTypeArgumentBinder;

import wbh.bookworm.hoerbuchdienst.domain.required.audiobookrepository.Databeat;

@Singleton
final class DatabeatTypeBinder implements RabbitTypeArgumentBinder<Databeat> {

    private final ObjectMapper objectMapper;

    @Inject
    DatabeatTypeBinder(final ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public Argument<Databeat> argumentType() {
        return Argument.of(Databeat.class);
    }

    @Override
    public BindingResult<Databeat> bind(final ArgumentConversionContext<Databeat> context,
                                        final RabbitConsumerState source) {
        try {
            final Databeat databeat = objectMapper.readValue(source.getBody(), Databeat.class);
            return () -> Optional.of(databeat);
        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        }
        /*return new BindingResult<>() {
            @Override
            public Optional<Heartbeat> getValue() {
                return Optional.empty();
            }

            @Override
            public List<ConversionError> getConversionErrors() {
                return Collections.emptyList();
            }
        };*/
    }

}

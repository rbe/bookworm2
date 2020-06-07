package wbh.bookworm.hoerbuchdienst.adapter.required.daisy.audiobookrepository;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.IOException;
import java.util.Optional;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.micronaut.configuration.rabbitmq.bind.RabbitConsumerState;
import io.micronaut.configuration.rabbitmq.bind.RabbitTypeArgumentBinder;
import io.micronaut.core.convert.ArgumentConversionContext;
import io.micronaut.core.type.Argument;

import wbh.bookworm.hoerbuchdienst.domain.required.audiobookrepository.DataHeartbeat;

@Singleton
final class DataHearbeatTypeBinder implements RabbitTypeArgumentBinder<DataHeartbeat> {

    private final ObjectMapper objectMapper;

    @Inject
    DataHearbeatTypeBinder(final ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public Argument<DataHeartbeat> argumentType() {
        return Argument.of(DataHeartbeat.class);
    }

    @Override
    public BindingResult<DataHeartbeat> bind(final ArgumentConversionContext<DataHeartbeat> context,
                                             final RabbitConsumerState source) {
        try {
            final DataHeartbeat dataHeartbeat = objectMapper.readValue(source.getBody(), DataHeartbeat.class);
            return () -> Optional.of(dataHeartbeat);
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

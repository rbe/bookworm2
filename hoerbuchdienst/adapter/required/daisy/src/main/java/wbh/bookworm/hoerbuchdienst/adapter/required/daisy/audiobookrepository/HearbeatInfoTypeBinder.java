package wbh.bookworm.hoerbuchdienst.adapter.required.daisy.audiobookrepository;

import javax.inject.Singleton;
import java.io.IOException;
import java.util.Optional;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.micronaut.configuration.rabbitmq.bind.RabbitConsumerState;
import io.micronaut.configuration.rabbitmq.bind.RabbitTypeArgumentBinder;
import io.micronaut.core.convert.ArgumentConversionContext;
import io.micronaut.core.type.Argument;

@Singleton
final class HearbeatInfoTypeBinder implements RabbitTypeArgumentBinder<HeartbeatInfo> {

    private final ObjectMapper objectMapper;

    HearbeatInfoTypeBinder(final ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public Argument<HeartbeatInfo> argumentType() {
        return Argument.of(HeartbeatInfo.class);
    }

    @Override
    public BindingResult<HeartbeatInfo> bind(final ArgumentConversionContext<HeartbeatInfo> context, final RabbitConsumerState source) {
        try {
            final HeartbeatInfo heartbeatInfo = objectMapper.readValue(source.getBody(), HeartbeatInfo.class);
            return () -> Optional.of(heartbeatInfo);
        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        }
        /*return new BindingResult<>() {
            @Override
            public Optional<HeartbeatInfo> getValue() {
                return Optional.empty();
            }

            @Override
            public List<ConversionError> getConversionErrors() {
                return Collections.emptyList();
            }
        };*/
    }

}

package wbh.bookworm.hoerbuchdienst.adapter.required.daisy.audiobookrepository;

import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.IOException;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.micronaut.core.type.Argument;
import io.micronaut.rabbitmq.bind.RabbitConsumerState;
import io.micronaut.rabbitmq.intercept.MutableBasicProperties;
import io.micronaut.rabbitmq.serdes.RabbitMessageSerDes;

import wbh.bookworm.hoerbuchdienst.domain.required.audiobookrepository.Databeat;

@Singleton
final class DatabeatSerDes implements RabbitMessageSerDes<Databeat> {

    private final ObjectMapper objectMapper;

    @Inject
    DatabeatSerDes(final ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Nullable
    @Override
    public Databeat deserialize(final RabbitConsumerState consumerState,
                                final Argument<Databeat> argument) {
        try {
            return objectMapper.readValue(consumerState.getBody(), Databeat.class);
        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        }
    }

    @Nullable
    @Override
    public byte[] serialize(@Nullable final Databeat data,
                            final MutableBasicProperties properties) {
        if (null != data) {
            try {
                return objectMapper.writeValueAsBytes(data);
            } catch (IOException e) {
                throw new IllegalArgumentException(e);
            }
        } else {
            return new byte[0];
        }
    }

    @Override
    public boolean supports(final Argument<Databeat> argument) {
        return argument.getType().isAssignableFrom(Databeat.class);
    }

}

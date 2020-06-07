package wbh.bookworm.hoerbuchdienst.adapter.required.daisy.audiobookrepository;

import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.IOException;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.micronaut.configuration.rabbitmq.bind.RabbitConsumerState;
import io.micronaut.configuration.rabbitmq.intercept.MutableBasicProperties;
import io.micronaut.configuration.rabbitmq.serdes.RabbitMessageSerDes;
import io.micronaut.core.type.Argument;

import wbh.bookworm.hoerbuchdienst.domain.required.audiobookrepository.DataHeartbeat;

@Singleton
final class DataHeartbeatSerDes implements RabbitMessageSerDes<DataHeartbeat> {

    private final ObjectMapper objectMapper;

    @Inject
    DataHeartbeatSerDes(final ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Nullable
    @Override
    public DataHeartbeat deserialize(final RabbitConsumerState consumerState,
                                     final Argument<DataHeartbeat> argument) {
        try {
            return objectMapper.readValue(consumerState.getBody(), DataHeartbeat.class);
        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        }
    }

    @Nullable
    @Override
    public byte[] serialize(@Nullable final DataHeartbeat data,
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
    public boolean supports(final Argument<DataHeartbeat> argument) {
        return argument.getType().isAssignableFrom(DataHeartbeat.class);
    }

}

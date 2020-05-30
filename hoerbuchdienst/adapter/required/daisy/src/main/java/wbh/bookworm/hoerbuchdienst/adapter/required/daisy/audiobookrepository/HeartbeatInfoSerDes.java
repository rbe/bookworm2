package wbh.bookworm.hoerbuchdienst.adapter.required.daisy.audiobookrepository;

import javax.annotation.Nullable;
import javax.inject.Singleton;
import java.io.IOException;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.micronaut.configuration.rabbitmq.bind.RabbitConsumerState;
import io.micronaut.configuration.rabbitmq.intercept.MutableBasicProperties;
import io.micronaut.configuration.rabbitmq.serdes.RabbitMessageSerDes;
import io.micronaut.core.type.Argument;

@Singleton
final class HeartbeatInfoSerDes implements RabbitMessageSerDes<HeartbeatInfo> {

    private final ObjectMapper objectMapper;

    public HeartbeatInfoSerDes() {
        objectMapper = new ObjectMapper();
    }

    @Nullable
    @Override
    public HeartbeatInfo deserialize(final RabbitConsumerState consumerState, final Argument<HeartbeatInfo> argument) {
        try {
            return objectMapper.readValue(consumerState.getBody(), HeartbeatInfo.class);
        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        }
    }

    @Nullable
    @Override
    public byte[] serialize(@Nullable final HeartbeatInfo data, final MutableBasicProperties properties) {
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
    public boolean supports(final Argument<HeartbeatInfo> argument) {
        return argument.getType().isAssignableFrom(HeartbeatInfo.class);
    }

}

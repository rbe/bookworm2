package wbh.bookworm.hoerbuchdienst.domain.required.audiobookrepository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.Collections;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

@MicronautTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class DatabeatSerializationTest {

    private final ObjectMapper objectMapper;

    public DatabeatSerializationTest() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    @Test
    @Order(1)
    void shouldSerializeDatabeat() throws IOException {
        final Databeat databeat = new Databeat(Instant.now(), new ShardName(),
                1_000_00L, 5_000L,
                Collections.emptyList(),
                "a-hash");
        try {
            final String value = objectMapper.writeValueAsString(databeat);
            Files.writeString(Path.of("target/Databeat.json"), value);
        } catch (IOException e) {
            throw e;
        }
    }

    @Test
    @Order(2)
    void shouldDeserializeDatabeat() throws IOException {
        try {
            final String value = Files.readString(Path.of("target/Databeat.json"));
            final Databeat databeat = objectMapper.readValue(value, Databeat.class);
        } catch (IOException e) {
            throw e;
        }
    }

}

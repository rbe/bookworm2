package wbh.bookworm.hoerbuchkatalog.repository.downloads;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
class RedisDownloadsRepositoryTest {

    @Test
    void shouldPersistDownload() {
    }

    @Configuration
    public static class TestConfiguration {
    }

}

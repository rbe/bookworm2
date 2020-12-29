package wbh.bookworm.hoerbuchkatalog.repository.downloads;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import wbh.bookworm.hoerbuchkatalog.domain.bestellung.Downloads;
import wbh.bookworm.hoerbuchkatalog.domain.bestellung.DownloadsId;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
class DownloadsRepositoryTest {

    @Test
    void shouldPersistDownload() {
    }

}

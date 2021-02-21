package wbh.bookworm.hoerbuchkatalog.webservice.katalog;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ActiveProfiles;

import wbh.bookworm.hoerbuchkatalog.app.katalog.HoerbuchkatalogService;
import wbh.bookworm.hoerbuchkatalog.domain.katalog.Hoerbuch;
import wbh.bookworm.hoerbuchkatalog.domain.lieferung.Belastung;
import wbh.bookworm.hoerbuchkatalog.repository.config.RepositoryResolver;
import wbh.bookworm.shared.domain.Hoerernummer;

@SpringBootTest
@ActiveProfiles("test")
class HoerbuchMapperTest {

    private final HoerbuchkatalogService hoerbuchkatalogService;

    @Autowired
    HoerbuchMapperTest(final HoerbuchkatalogService hoerbuchkatalogService) {
        this.hoerbuchkatalogService = hoerbuchkatalogService;
    }

    @Test
    void shouldConvert() {
        final List<Belastung> belastungen = new ArrayList<>();
        belastungen.add(new Belastung(LocalDate.now(), "999", "21052"));
        Assertions.assertThat(belastungen).isNotNull();
        final Hoerbuch hoerbuch = hoerbuchkatalogService.hole(new Hoerernummer("80170"), belastungen.get(0).getTitelnummer());
        Assertions.assertThat(hoerbuch).isNotNull();
        final HoerbuchInfo dto = HoerbuchMapper.INSTANCE.convertToHoerbuchAntwortKurzDto(hoerbuch);
        Assertions.assertThat(dto)
                .isNotNull()
                .hasFieldOrPropertyWithValue("sachgebiet", "I")
                .hasFieldOrPropertyWithValue("sachgebietBezeichnung", "Science Fiction - Phantastische Literatur");
    }

    @Configuration
    @ComponentScan(basePackageClasses = {
            HoerbuchkatalogService.class,
            RepositoryResolver.class
    })
    public static class TestConfiguration {

    }

}

/*
 * Copyright (C) 2011-2018 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchkatalog.app.email;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import wbh.bookworm.hoerbuchkatalog.domain.bestellung.Bestellung;
import wbh.bookworm.hoerbuchkatalog.domain.bestellung.BestellungId;
import wbh.bookworm.hoerbuchkatalog.domain.hoerer.HoererEmail;
import wbh.bookworm.hoerbuchkatalog.domain.hoerer.Hoerername;
import wbh.bookworm.hoerbuchkatalog.domain.hoerer.Nachname;
import wbh.bookworm.hoerbuchkatalog.domain.hoerer.Vorname;
import wbh.bookworm.hoerbuchkatalog.domain.katalog.Hoerbuch;
import wbh.bookworm.shared.domain.AghNummer;
import wbh.bookworm.shared.domain.Hoerernummer;
import wbh.bookworm.shared.domain.Sachgebiet;
import wbh.bookworm.shared.domain.Titelnummer;

import static org.junit.jupiter.api.Assertions.assertLinesMatch;

@SpringBootTest(classes = {EmailTestAppConfig.class})
@ExtendWith({SpringExtension.class})
@Disabled("TODO onfiguration")
class TemplateBuilderTest {

    private final EmailTemplateBuilder emailTemplateBuilder;

    @Autowired
    TemplateBuilderTest(final EmailTemplateBuilder emailTemplateBuilder) {
        this.emailTemplateBuilder = emailTemplateBuilder;
    }

    @Test
    void shouldBuildSimpleMessage() {
        assertLinesMatch(
                Arrays.asList(
                        "<!DOCTYPE html>",
                        "<html lang=\"en\" xmlns=\"http://www.w3.org/1999/xhtml\">",
                        "<head></head>",
                        "<body>",
                        "    <span>This is a test message.</span>",
                        "</body>",
                        "</html>"),
                Arrays.asList(
                        emailTemplateBuilder.buildSimple("This is a test message.")
                                .split("\n"))
        );
    }

    @Test
    void shouldBuildBestellbestaetigungCd() throws IOException {
        final Bestellung bestellung = new Bestellung(
                new BestellungId("1234567890"),
                new Hoerernummer("80170"),
                new Hoerername(new Vorname("Herbert"), new Nachname("Hörer")),
                new HoererEmail("herbert.hoerer@example.com"),
                "Bemerkung",
                Boolean.FALSE, Boolean.FALSE,
                Set.of(new Titelnummer("123456"), new Titelnummer("789012")),
                null,
                LocalDateTime.now()
        );
        final Set<Hoerbuch> hoerbucher = hoerbuecher();
        final String build = emailTemplateBuilder.build("BestellbestaetigungCd.html",
                Map.of("bestellung", bestellung, "hoerbuecher", hoerbucher));
        Files.write(Path.of("target/BestellbestaetigungCd-test.html"),
                build.getBytes(StandardCharsets.UTF_8));
    }

    @Test
    void shouldBuildBestellbestaetigungDownload() throws IOException {
        final Bestellung bestellung = new Bestellung(
                new BestellungId("1234567890"),
                new Hoerernummer("80170"),
                new Hoerername(new Vorname("Herbert"), new Nachname("Hörer")),
                new HoererEmail("herbert.hoerer@example.com"),
                "Bemerkung",
                Boolean.FALSE, Boolean.FALSE,
                null,
                Set.of(new Titelnummer("123456"), new Titelnummer("789012")),
                LocalDateTime.now()
        );
        final Set<Hoerbuch> hoerbucher = hoerbuecher();
        final String build = emailTemplateBuilder.build("BestellbestaetigungDownload.html",
                Map.of("bestellung", bestellung, "hoerbuecher", hoerbucher));
        Files.write(Path.of("target/BestellbestaetigungDownload-test.html"),
                build.getBytes(StandardCharsets.UTF_8));
    }

    private Set<Hoerbuch> hoerbuecher() {
        return Set.of(
                    new Hoerbuch(Sachgebiet.A, new Titelnummer("123456"),
                            "Autor Autor Autor", "Titel Titel Titel Titel Titel", "", "",
                            "", "", "", "", "", "",
                            "", "", "", "", "",
                            LocalDate.now(), new AghNummer("1-1234567-2-3"), false),
                    new Hoerbuch(Sachgebiet.A, new Titelnummer("234567"),
                            "Autor Autor Autor", "Titel Titel Titel Titel Titel", "", "",
                            "", "", "", "", "", "",
                            "", "", "", "", "",
                            LocalDate.now(), new AghNummer("1-1234567-2-3"), false),
                    new Hoerbuch(Sachgebiet.A, new Titelnummer("345678"),
                            "Autor Autor Autor", "Titel Titel Titel Titel Titel", "", "",
                            "", "", "", "", "", "",
                            "", "", "", "", "",
                            LocalDate.now(), new AghNummer("1-1234567-2-3"), false));
    }

}

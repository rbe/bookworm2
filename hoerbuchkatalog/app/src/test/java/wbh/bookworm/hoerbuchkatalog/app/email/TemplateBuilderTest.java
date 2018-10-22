/*
 * Copyright (C) 2011-2018 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchkatalog.app.email;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertLinesMatch;

@SpringBootTest(classes = {EmailTestAppConfig.class})
@ExtendWith({SpringExtension.class})
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

}

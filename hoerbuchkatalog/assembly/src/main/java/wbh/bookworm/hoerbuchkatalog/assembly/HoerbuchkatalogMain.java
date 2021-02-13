/*
 * Copyright (C) 2011-2018 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchkatalog.assembly;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;

@SpringBootApplication(scanBasePackages = {
        "wbh.bookworm.hoerbuchkatalog"
})
@ServletComponentScan
@OpenAPIDefinition(info = @Info(
        title = "Hoerbuchkatalog",
        version = "1.0.0",
        description = "Hoerbuchkatalog",
        license = @License(name = "All rights reserved", url = "https://www.art-of-coding.eu"),
        contact = @Contact(url = "https://www.art-of-coding.eu", name = "Ralf", email = "ralf@art-of-coding.eu")
))
public class HoerbuchkatalogMain {

    public static void main(String[] args) {
        SpringApplication.run(HoerbuchkatalogMain.class, args);
    }

}

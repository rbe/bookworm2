/*
 * Copyright (C) 2011-2018 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchkatalog.assembly;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;

@SpringBootApplication(scanBasePackages = {
        "wbh.bookworm.hoerbuchkatalog"
})
@ServletComponentScan
public class HoerbuchkatalogMain {

    public static void main(String[] args) {
        SpringApplication.run(HoerbuchkatalogMain.class, args);
    }

}

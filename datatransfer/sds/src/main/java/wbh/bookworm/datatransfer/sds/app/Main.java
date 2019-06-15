/*
 * Copyright (C) 2011-2019 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.datatransfer.sds.app;

import wbh.bookworm.datatransfer.sds.transfer.SecureStore;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(scanBasePackageClasses = {
        SecureStore.class
})
@EnableScheduling
public class Main /*extends SpringBootServletInitializer*/ {

    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }

}

/*
 * Copyright (C) 2011-2018 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchkatalog.ui.config;

import wbh.bookworm.hoerbuchkatalog.app.bestellung.BestellungService;
import wbh.bookworm.hoerbuchkatalog.app.hoerer.HoererService;
import wbh.bookworm.hoerbuchkatalog.app.katalog.HoerbuchkatalogService;
import wbh.bookworm.hoerbuchkatalog.app.lieferung.DownloadsLieferungService;
import wbh.bookworm.hoerbuchkatalog.ui.katalog.AuthenticationFilter;
import wbh.bookworm.hoerbuchkatalog.ui.katalog.Navigation;
import wbh.bookworm.hoerbuchkatalog.ui.nutzerdaten.MeineDaten;

import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackageClasses = {
        HoerbuchkatalogService.class,
        BestellungService.class,
        DownloadsLieferungService.class,
        HoererService.class,
        Navigation.class,
        MeineDaten.class
})
@ServletComponentScan(basePackageClasses = {
        CachingFilter.class,
        AuthenticationFilter.class
})
public class UiConfig {
}

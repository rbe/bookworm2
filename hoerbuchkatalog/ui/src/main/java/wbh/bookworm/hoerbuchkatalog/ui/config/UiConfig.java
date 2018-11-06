/*
 * Copyright (C) 2011-2018 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchkatalog.ui.config;

import wbh.bookworm.hoerbuchkatalog.app.bestellung.BestellungService;
import wbh.bookworm.hoerbuchkatalog.app.katalog.HoerbuchkatalogService;
import wbh.bookworm.hoerbuchkatalog.app.lieferung.HoererLieferungService;
import wbh.bookworm.hoerbuchkatalog.domain.hoerer.Hoerer;
import wbh.bookworm.hoerbuchkatalog.domain.hoerer.HoererEmail;
import wbh.bookworm.hoerbuchkatalog.domain.hoerer.Hoerername;
import wbh.bookworm.hoerbuchkatalog.domain.hoerer.Hoerernummer;
import wbh.bookworm.hoerbuchkatalog.domain.hoerer.Nachname;
import wbh.bookworm.hoerbuchkatalog.domain.hoerer.Vorname;
import wbh.bookworm.hoerbuchkatalog.ui.Navigation;
import wbh.bookworm.hoerbuchkatalog.ui.http.NoCacheFilter;

import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@Configuration
@ComponentScan(basePackageClasses = {
        HoerbuchkatalogService.class,
        BestellungService.class,
        HoererLieferungService.class,
        Navigation.class
})
@ServletComponentScan(basePackageClasses = {
        NoCacheFilter.class
})
public class UiConfig {

    private static final String HOERERNUMMER = "hnr";

    @Bean
    @Lazy
    public Hoerernummer hoerernummer() {
        return (Hoerernummer) getSession().getAttribute(HOERERNUMMER);
    }

    @Bean
    @Lazy
    public Hoerer hoerer() {
        return new Hoerer(hoerernummer(),
                new Hoerername(new Vorname("VORNAME"), new Nachname("NACHNAME")),
                new HoererEmail("hoerer@example.com"));
    }

    private HttpSession getSession() {
        return ((HttpServletRequest) FacesContext.getCurrentInstance()
                .getExternalContext()
                .getRequest())
                .getSession();
    }

}

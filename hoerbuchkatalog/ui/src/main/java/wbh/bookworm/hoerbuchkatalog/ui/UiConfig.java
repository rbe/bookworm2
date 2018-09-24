/*
 * Copyright (C) 2011-2018 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchkatalog.ui;

import wbh.bookworm.hoerbuchkatalog.app.bestellung.BestellungService;
import wbh.bookworm.hoerbuchkatalog.app.katalog.HoerbuchkatalogService;
import wbh.bookworm.hoerbuchkatalog.app.lieferung.LieferungService;
import wbh.bookworm.hoerbuchkatalog.domain.bestellung.BestellungFactory;
import wbh.bookworm.hoerbuchkatalog.domain.hoerer.Hoerernummer;

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
        UiConfig.class,
        HoerbuchkatalogService.class,
        BestellungService.class,
        BestellungFactory.class,
        LieferungService.class
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

    private HttpSession getSession() {
        return ((HttpServletRequest) FacesContext.getCurrentInstance()
                .getExternalContext()
                .getRequest())
                .getSession();
    }

}

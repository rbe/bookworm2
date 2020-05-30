/*
 * Copyright (C) 2011-2018 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchkatalog.ui.config;

import javax.servlet.DispatcherType;
import java.util.EnumSet;

import org.omnifaces.filter.FacesExceptionFilter;
import org.springframework.boot.web.server.ErrorPage;
import org.springframework.boot.web.server.ErrorPageRegistrar;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;

//@Configuration
public class FacesSupportConfig {

    private static final String ERROR_PAGE = "support.xhtml";

    @Bean
    public FilterRegistrationBean filterRegistrationBean() {
        final FilterRegistrationBean<FacesExceptionFilter> filterRegBean = new FilterRegistrationBean<>();
        filterRegBean.setFilter(new FacesExceptionFilter());
        filterRegBean.setEnabled(Boolean.TRUE);
        filterRegBean.addUrlPatterns("/*");
        filterRegBean.setDispatcherTypes(EnumSet.allOf(DispatcherType.class));
        return filterRegBean;
    }

    @Bean
    public ErrorPageRegistrar errorPageRegistrar() {
        return registry -> {
            registry.addErrorPages(new ErrorPage(HttpStatus.NOT_FOUND, ERROR_PAGE));
            registry.addErrorPages(new ErrorPage(HttpStatus.INTERNAL_SERVER_ERROR, ERROR_PAGE));
        };
    }

}

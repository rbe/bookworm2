/*
 * Copyright (C) 2011-2019 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchkatalog.repository.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import wbh.bookworm.hoerbuchkatalog.repository.katalog.Hoerbuchkatalog;
import wbh.bookworm.hoerbuchkatalog.repository.katalog.HoerbuchkatalogRepository;
import wbh.bookworm.hoerbuchkatalog.repository.lieferung.CdLieferungRepository;
import wbh.bookworm.hoerbuchkatalog.repository.nutzerdaten.HoererRepository;

@Component
public final class RepositoryResolver {

    private static final Logger LOGGER = LoggerFactory.getLogger(RepositoryResolver.class);

    private final ApplicationContext applicationContext;

    @Autowired
    public RepositoryResolver(final ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    private <T> T getBean(Class<T> klass) {
        LOGGER.trace("Trying to get {} from ApplicationContext", klass);
        final T bean = applicationContext.getBean(klass);
        LOGGER.trace("Got {} for {} from ApplicationContext", bean, klass);
        return bean;
    }

    public HoerbuchkatalogRepository hoerbuchkatalogRepository() {
        return getBean(HoerbuchkatalogRepository.class);
    }

    public Hoerbuchkatalog hoerbuchkatalog() {
        return getBean(Hoerbuchkatalog.class);
    }

    public HoererRepository hoererRepository() {
        return getBean(HoererRepository.class);
    }

    public CdLieferungRepository cdLieferungRepository() {
        return getBean(CdLieferungRepository.class);
    }

    /*
    public DownloadsRepository downloadsRepository() {
        return getBean(DownloadsRepository.class);
    }
    */

}

/*
 * Copyright (C) 2011-2018 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchkatalog.repository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

@Component
class RepositoryBeanPostProcessor implements BeanPostProcessor {

    private static final Logger LOGGER = LoggerFactory.getLogger(RepositoryBeanPostProcessor.class);

    private final ApplicationContext applicationContext;

    @Autowired
    RepositoryBeanPostProcessor(final ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Override
    public Object postProcessAfterInitialization(final Object bean, final String beanName) throws BeansException {
        if (bean instanceof HoerbuchkatalogFactory) {
            LOGGER.debug("{}/{}", beanName, bean);
            final HoerbuchkatalogFactory hoerbuchkatalogFactory = (HoerbuchkatalogFactory) bean;
            hoerbuchkatalogFactory.archivRegelmaessigAktualisieren();
        }
        return bean;
    }

}

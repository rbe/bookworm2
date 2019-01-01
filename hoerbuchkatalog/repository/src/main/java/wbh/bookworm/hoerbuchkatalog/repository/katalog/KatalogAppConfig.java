/*
 * Copyright (C) 2011-2018 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchkatalog.repository.katalog;

import wbh.bookworm.hoerbuchkatalog.domain.config.DomainConfig;
import wbh.bookworm.hoerbuchkatalog.domain.katalog.Hoerbuch;
import wbh.bookworm.hoerbuchkatalog.domain.katalog.Titelnummer;

import aoc.ddd.search.LuceneIndex;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static org.springframework.beans.factory.config.ConfigurableBeanFactory.SCOPE_PROTOTYPE;

@Configuration
@ComponentScan(basePackageClasses = {
        HoerbuchkatalogRepository.class,
        LuceneIndex.class,
        DomainConfig.class
})
@EnableScheduling
@EnableConfigurationProperties
public class KatalogAppConfig {

    static final String HOERBUCHKATALOG_MAP = "hoerbuchkatalogMap";

    @Bean(HOERBUCHKATALOG_MAP)
    @Scope(SCOPE_PROTOTYPE)
    public Map<Titelnummer, Hoerbuch> hoerbuchkatalogMap() {
        return new ConcurrentHashMap<>();
    }

    @Bean
    @ConditionalOnMissingBean
    public TaskExecutor taskExecutor() {
        final ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
        taskExecutor.setCorePoolSize(5);
        taskExecutor.setMaxPoolSize(10);
        taskExecutor.setThreadNamePrefix("hoerbuchkatalogRepositoryExecutor-");
        taskExecutor.initialize();
        return taskExecutor;
    }

    @Bean
    @ConditionalOnMissingBean
    public TaskScheduler taskScheduler() {
        return new ThreadPoolTaskScheduler();
    }

}

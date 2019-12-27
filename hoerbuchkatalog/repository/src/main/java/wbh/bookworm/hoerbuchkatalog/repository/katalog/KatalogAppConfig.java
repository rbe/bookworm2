/*
 * Copyright (C) 2011-2018 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchkatalog.repository.katalog;

import wbh.bookworm.hoerbuchkatalog.domain.config.DomainConfig;

import aoc.mikrokosmos.ddd.search.LuceneIndex;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@ComponentScan(basePackageClasses = {
        HoerbuchkatalogRepository.class,
        LuceneIndex.class,
        DomainConfig.class
})
@EnableScheduling
@EnableConfigurationProperties
public class KatalogAppConfig {

/*
    @Bean
    @ConditionalOnMissingBean({ThreadPoolTaskExecutor.class})
    public TaskExecutor threadPoolTaskExecutor() {
        final ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
        taskExecutor.setThreadNamePrefix("hoerbuchkatalogRepositoryExecutor-");
        taskExecutor.setCorePoolSize(Runtime.getRuntime().availableProcessors());
        taskExecutor.setMaxPoolSize(Runtime.getRuntime().availableProcessors() * 10);
        taskExecutor.initialize();
        return taskExecutor;
    }

    @Bean
    @ConditionalOnMissingBean({ThreadPoolTaskScheduler.class})
    public TaskScheduler threadPoolTaskScheduler() {
        return new ThreadPoolTaskScheduler();
    }
*/

}

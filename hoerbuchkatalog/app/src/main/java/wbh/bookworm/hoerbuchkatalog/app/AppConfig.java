/*
 * eu.artofcoding.bookworm
 *
 * Copyright (C) 2011-2017 art of coding UG, http://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchkatalog.app;

import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import wbh.bookworm.hoerbuchkatalog.domain.Hoerbuch;
import wbh.bookworm.hoerbuchkatalog.domain.Titelnummer;
import wbh.bookworm.hoerbuchkatalog.ui.NoCacheFilter;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Configuration
@ComponentScan({
        "wbh.bookworm.hoerbuchkatalog.ui",
        "wbh.bookworm.hoerbuchkatalog.repository",
        "wbh.bookworm.hoerbuchkatalog.domain"
})
@EnableScheduling
public class AppConfig {

    @Bean
    static PropertyPlaceholderConfigurer bookwormProperties() {
        final PropertyPlaceholderConfigurer propertyPlaceholderConfigurer = new PropertyPlaceholderConfigurer();
        propertyPlaceholderConfigurer.setLocations(new ClassPathResource("/conf/hoerbuchkatalog.properties"));
        return propertyPlaceholderConfigurer;
    }

    /*
    @Bean
    static YamlPropertySourceLoader bookwormYaml() throws IOException {
        YamlPropertySourceLoader yamlPropertySourceLoader = new YamlPropertySourceLoader();
        final List<PropertySource<?>> bookworm =
            yamlPropertySourceLoader.load("bookworm", new ClassPathResource("/conf/hoerbuchkatalog.yaml"));
        return yamlPropertySourceLoader;
    }
    */

    @Bean
    public Map<Titelnummer, Hoerbuch> hoerbuchkatalogMap() {
        return new ConcurrentHashMap<>();
    }

    @Bean
    public TaskExecutor taskExecutor() {
        final ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
        taskExecutor.setCorePoolSize(5);
        taskExecutor.setMaxPoolSize(10);
        taskExecutor.setThreadNamePrefix("hoerbuchkatalogExecutor-");
        taskExecutor.initialize();
        return taskExecutor;
    }

    @Bean
    public TaskScheduler taskScheduler() {
        return new ThreadPoolTaskScheduler();
    }

    @Bean
    public FilterRegistrationBean noCacheFilter() {
        FilterRegistrationBean registration = new FilterRegistrationBean();
        registration.setFilter(new NoCacheFilter());
        registration.addServletNames("FacesServlet");
        return registration;
    }

}

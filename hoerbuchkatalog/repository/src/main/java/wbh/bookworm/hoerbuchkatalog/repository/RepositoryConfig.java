/*
 * Copyright (C) 2011-2018 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchkatalog.repository;

import wbh.bookworm.hoerbuchkatalog.domain.katalog.Hoerbuch;
import wbh.bookworm.hoerbuchkatalog.domain.katalog.Titelnummer;
import wbh.bookworm.platform.ddd.repository.search.LuceneIndex;

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
        RepositoryConfig.class,
        LuceneIndex.class
})
@EnableScheduling
public class RepositoryConfig {

    @Bean("hoerbuchkatalogMap")
    @Scope(SCOPE_PROTOTYPE)
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

    /*
    @Bean
    @Scope("singleton")
    public HazelcastInstance hazelcastInstance() {
        final Config config = new Config(this.getClass().getPackage().getName());
        final InterfacesConfig interfaces =
                new InterfacesConfig()
                        .setEnabled(true)
                        .addInterface("127.0.0.1");
        final NetworkConfig networkConfig =
                new NetworkConfig()
                        .setPublicAddress("127.0.0.1")
                        .setInterfaces(interfaces);
        networkConfig.getJoin()
                .getMulticastConfig()
                .setEnabled(false);
        networkConfig.getJoin()
                .getTcpIpConfig()
                .setEnabled(true)
                .addMember("127.0.0.1");
        config.setNetworkConfig(networkConfig);
        final ManagementCenterConfig managementCenterConfig =
                new ManagementCenterConfig()
                        .setEnabled(true)
                        .setUrl("http://localhost:8080/mancenter");
        config.setManagementCenterConfig(managementCenterConfig);
        return Hazelcast.newHazelcastInstance(config);
    }

    @Bean
    @Autowired
    public Map<Titelnummer, Hoerbuch> hoerbuchkatalogMap(HazelcastInstance hazelcastInstance) {
        return hazelcastInstance.getMap("hoerbuchkatalog");
    }
    */

}

/*
 * eu.artofcoding.bookworm
 *
 * Copyright (C) 2011-2017 art of coding UG, http://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchkatalog.app;

import org.springframework.context.annotation.Configuration;

@Configuration
public class HazelcastConfig {

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

    @Bean
    @Autowired
    public Set<AghNummer> blistaCatalog(HazelcastInstance hazelcastInstance) {
        return hazelcastInstance.getSet("blistaAghNummern");
    }
    */

}

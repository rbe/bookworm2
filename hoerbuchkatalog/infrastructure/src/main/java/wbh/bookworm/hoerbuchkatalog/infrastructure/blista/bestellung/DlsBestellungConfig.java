/*
 * Copyright (C) 2011-2018 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchkatalog.infrastructure.blista.bestellung;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@Component
@PropertySource({"classpath:/conf/blista-dls-bestellung.properties"})
@ConfigurationProperties(prefix = "blista.dls.bestellung.sftp")
final class DlsBestellungConfig {

    private String host;

    private Integer port;

    private String bibliothek;

    private String bibkennwort;

    String getHost() {
        return host;
    }

    Integer getPort() {
        return port;
    }

    String getBibliothek() {
        return bibliothek;
    }

    String getBibkennwort() {
        return bibkennwort;
    }

}

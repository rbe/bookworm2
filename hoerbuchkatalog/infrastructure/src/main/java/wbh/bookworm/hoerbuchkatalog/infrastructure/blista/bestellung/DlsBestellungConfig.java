/*
 * Copyright (C) 2011-2018 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchkatalog.infrastructure.blista.bestellung;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@Component
@PropertySource({"classpath:/conf/blista-dls-bestellung.properties"})
final class DlsBestellungConfig {

    @Value("${blista.dls.bestellung.sftp.host}")
    private String sftpHost;

    @Value("${blista.dls.bestellung.sftp.port}")
    private Integer sftpPort;

    @Value("${blista.dls.bestellung.sftp.bibliothek}")
    private String bibliothek;

    @Value("${blista.dls.bestellung.sftp.bibkennwort}")
    private String bibkennwort;

    String getSftpHost() {
        return sftpHost;
    }

    Integer getSftpPort() {
        return sftpPort;
    }

    String getBibliothek() {
        return bibliothek;
    }

    String getBibkennwort() {
        return bibkennwort;
    }

}

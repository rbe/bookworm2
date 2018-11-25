/*
 * Copyright (C) 2011-2018 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchkatalog.infrastructure.blista.lieferung;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import java.nio.file.Path;

@Component
@PropertySource({"classpath:/conf/blista-dls-lieferung.properties"})
final class DlsLieferungConfig {

    @Value("${blista.dls.lieferung.directory}")
    private Path blistaDlsDirectory;

    //@Value("${blista.dls.lieferung.rest.url}")
    @Value("${blista.dls.lieferung.rest.scheme}://" +
            "${blista.dls.lieferung.rest.host}:${blista.dls.lieferung.rest.port}" +
            "${blista.dls.lieferung.rest.path}")
    private String blistaDlsRestUrl;

    @Value("${blista.dls.lieferung.rest.bibliothek}")
    private String bibliothek;

    @Value("${blista.dls.lieferung.rest.bibkennwort}")
    private String bibkennwort;

    Path getBlistaDlsDirectory() {
        return blistaDlsDirectory;
    }

    String getBlistaDlsRestUrl() {
        return blistaDlsRestUrl;
    }

    String getBibliothek() {
        return bibliothek;
    }

    String getBibkennwort() {
        return bibkennwort;
    }

}

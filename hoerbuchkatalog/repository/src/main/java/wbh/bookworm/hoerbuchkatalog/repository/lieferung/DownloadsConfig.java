/*
 * Copyright (C) 2011-2018 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchkatalog.repository.lieferung;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.file.Path;

@Component
final class DownloadsConfig {

    @Value("${blista.dls.directory}")
    private Path blistaDlsDirectory;

    @Value("${blista.dlscatalog.rest.url}")
    private String blistaDlsCatalogRestUrl;

    Path getBlistaDlsDirectory() {
        return blistaDlsDirectory;
    }

    String getBlistaDlsCatalogRestUrl() {
        return blistaDlsCatalogRestUrl;
    }

}

/*
 * Copyright (C) 2011-2018 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchkatalog.repository.katalog;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.Charset;
import java.nio.file.Path;

@Component
final class HoerbuchkatalogConfig {

    @Value("${hoerbuchkatalog.directory}")
    private Path hoerbuchkatalogDirectory;

    @Value("${hoerbuchkatalog.cronexpression}")
    private String hoerbuchkatalogCronExpression;

    @Value("${wbh.gesamtdat.filename}")
    private String wbhGesamtdatFilename;

    @Value("${wbh.gesamtdat.charset}")
    private String wbhGesamtdatCharset;

    @Value("${blista.katalog.rest.url}")
    private String blistaDlsCatalogRestUrl;

    @Value("${blista.katalog.aghnummern.path_in_zip}")
    private String blistaDlsCatalogAghNummernPathInZip;

    Path getHoerbuchkatalogDirectory() {
        return hoerbuchkatalogDirectory;
    }

    String getWbhGesamtdatFilename() {
        return wbhGesamtdatFilename;
    }

    Charset getWbhGesamtdatCharset() {
        return Charset.forName(wbhGesamtdatCharset);
    }

    String getHoerbuchkatalogCronExpression() {
        return hoerbuchkatalogCronExpression;
    }

    String getBlistaKatalogRestUrl() {
        return blistaDlsCatalogRestUrl;
    }

    String getBlistaKatalogAghNummernPathInZip() {
        return blistaDlsCatalogAghNummernPathInZip;
    }

}

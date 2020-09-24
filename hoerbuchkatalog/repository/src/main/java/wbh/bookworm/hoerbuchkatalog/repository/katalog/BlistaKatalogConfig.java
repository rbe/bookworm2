/*
 * Copyright (C) 2011-2018 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchkatalog.repository.katalog;

import java.nio.charset.Charset;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "hoerbuchkatalog.blista")
public class BlistaKatalogConfig {

    public static class Rest {

        private String url;

        public void setUrl(final String url) {
            this.url = url;
        }

    }

    private Rest rest;

    public void setRest(final Rest rest) {
        this.rest = rest;
    }

    public static class AghNummern {

        private String pathinzip;

        public void setPathinzip(final String pathinzip) {
            this.pathinzip = pathinzip;
        }

        private Charset charset;

        public void setCharset(final Charset charset) {
            this.charset = charset;
        }

    }

    private AghNummern aghNummern;

    public void setAghNummern(final AghNummern aghNummern) {
        this.aghNummern = aghNummern;
    }

    String getKatalogRestUrl() {
        return rest.url;
    }

    String getAghNummernPathInZip() {
        return aghNummern.pathinzip;
    }

}

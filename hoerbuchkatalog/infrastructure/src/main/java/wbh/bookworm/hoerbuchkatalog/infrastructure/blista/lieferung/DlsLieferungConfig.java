/*
 * Copyright (C) 2011-2018 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchkatalog.infrastructure.blista.lieferung;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import java.nio.file.Path;

@Component
@PropertySource({"classpath:/conf/blista-dls-lieferung.properties"})
@ConfigurationProperties(prefix = "blista.dls.lieferung")
final class DlsLieferungConfig {

    private Path directory;

    public void setDirectory(final Path directory) {
        this.directory = directory;
    }

    Path getDirectory() {
        return directory;
    }

    public static class Rest {

        private String url;

        public void setUrl(final String url) {
            this.url = url;
        }

        private String bibliothek;

        public void setBibliothek(final String bibliothek) {
            this.bibliothek = bibliothek;
        }

        private String bibkennwort;

        public void setBibkennwort(final String bibkennwort) {
            this.bibkennwort = bibkennwort;
        }

    }

    private Rest rest;

    public void setRest(final Rest rest) {
        this.rest = rest;
    }

    String getRestUrl() {
        return rest.url;
    }

    String getBibliothek() {
        return rest.bibliothek;
    }

    String getBibkennwort() {
        return rest.bibkennwort;
    }

}

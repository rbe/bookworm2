/*
 * Copyright (C) 2011-2018 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchkatalog.infrastructure.blista.restdlskatalog;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import java.nio.file.Path;

@Component
@PropertySource({"classpath:/conf/blista-dls.properties"})
@ConfigurationProperties(prefix = "blista.dls.rest")
public final class DlsRestConfig {

    private String url;

    public String getUrl() {
        return url;
    }

    public void setUrl(final String url) {
        this.url = url;
    }

    private String bibliothek;

    public String getBibliothek() {
        return bibliothek;
    }

    public void setBibliothek(final String bibliothek) {
        this.bibliothek = bibliothek;
    }

    private String bibkennwort;

    public String getBibkennwort() {
        return bibkennwort;
    }

    public void setBibkennwort(final String bibkennwort) {
        this.bibkennwort = bibkennwort;
    }

    private Path directory;

    public Path getDirectory() {
        return directory;
    }

    public void setDirectory(final Path directory) {
        this.directory = directory;
    }

}

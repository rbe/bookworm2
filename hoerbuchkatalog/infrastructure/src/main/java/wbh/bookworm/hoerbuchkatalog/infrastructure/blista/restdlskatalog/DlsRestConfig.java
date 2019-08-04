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
import java.util.Arrays;

@Component
@PropertySource({"classpath:/conf/blista-dls.properties"})
@ConfigurationProperties(prefix = "blista.dls.rest")
/**
 * <pre>
 * #
 * # blista DLS - Abruf per REST
 * #
 *
 * blista.dls.rest.werkeurl=https://rest-dls-katalog.blista.de:443/v1/werke
 * blista.dls.rest.checkurl=https://rest-dls-katalog.blista.de:443/v1/check
 * blista.dls.rest.bibliothek=
 * blista.dls.rest.bibkennwort=
 * blista.dls.rest.directory=./var/blista/dls
 * </pre>
 */
public final class DlsRestConfig {

    private String werkeurl;

    public String getWerkeurl() {
        return werkeurl;
    }

    public void setWerkeurl(final String werkeurl) {
        this.werkeurl = werkeurl;
    }

    private String checkurl;

    public String getCheckurl() {
        return checkurl;
    }

    public void setCheckurl(final String checkurl) {
        this.checkurl = checkurl;
    }

    private char[] bibliothek;

    public char[] getBibliothek() {
        return bibliothek;
    }

    public void setBibliothek(final char[] bibliothek) {
        this.bibliothek = bibliothek;
    }

    private char[] bibkennwort;

    public char[] getBibkennwort() {
        return bibkennwort;
    }

    public void setBibkennwort(final char[] bibkennwort) {
        this.bibkennwort = bibkennwort;
    }

    private Path directory;

    public Path getDirectory() {
        return directory;
    }

    public void setDirectory(final Path directory) {
        this.directory = directory;
    }

    @Override
    public String toString() {
        return String.format("DlsRestConfig{werkeurl='%s', checkurl='%s', bibliothek=%s, bibkennwort=%d, directory=%s}",
                werkeurl, checkurl, Arrays.toString(bibliothek), bibkennwort.length, directory);
    }

}

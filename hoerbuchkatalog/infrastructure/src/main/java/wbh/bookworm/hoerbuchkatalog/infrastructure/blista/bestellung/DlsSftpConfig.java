/*
 * Copyright (C) 2011-2018 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchkatalog.infrastructure.blista.bestellung;

import java.nio.file.Path;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "blista.dls.sftp")
/**
 * <pre>
 * #
 * # blista DLS - Aufträge per SFTP
 * #
 *
 * blista.dls.sftp.host=rest-dls-katalog.blista.de
 * blista.dls.sftp.port=95
 * blista.dls.sftp.bibliothek=
 * blista.dls.sftp.bibkennwort=
 *
 * blista.dls.sftp.directory=./var/blista/dls
 * </pre>
 */
final class DlsSftpConfig {

    private String host;

    private Integer port;

    private String bibliothek;

    private String bibkennwort;

    private Path directory;

    String getHost() {
        return host;
    }

    public void setHost(final String host) {
        this.host = host;
    }

    Integer getPort() {
        return port;
    }

    public void setPort(final Integer port) {
        this.port = port;
    }

    String getBibliothek() {
        return bibliothek;
    }

    public void setBibliothek(final String bibliothek) {
        this.bibliothek = bibliothek;
    }

    String getBibkennwort() {
        return bibkennwort;
    }

    public void setBibkennwort(final String bibkennwort) {
        this.bibkennwort = bibkennwort;
    }

    public Path getDirectory() {
        return directory;
    }

    public void setDirectory(final Path directory) {
        this.directory = directory;
    }

    @Override
    public String toString() {
        return String.format("DlsSftpConfig{host='%s', port=%d, bibliothek='%s', bibkennwort='%s', directory=%s}",
                host, port, bibliothek, null != bibkennwort ? bibkennwort.length() : "-1", directory);
    }

}

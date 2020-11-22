/*
 * Copyright (C) 2011-2018 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchkatalog.repository.katalog;

import java.nio.charset.Charset;
import java.nio.file.Path;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "hoerbuchkatalog")
public class HoerbuchkatalogConfig {

    private Path directory;

    private Cron cron;

    private Wbh wbh;

    private Suchergebnisse suchergebnisse;

    Path getDirectory() {
        return directory;
    }

    public void setDirectory(final Path directory) {
        this.directory = directory;
    }

    public void setCron(final Cron cron) {
        this.cron = cron;
    }

    String getCronExpression() {
        return cron.expression;
    }

    public void setWbh(final Wbh wbh) {
        this.wbh = wbh;
    }

    String getWbhGesamtdatFilename() {
        return wbh.gesamtdat.filename;
    }

    Charset getWbhGesamtdatCharset() {
        return wbh.gesamtdat.charset;
    }

    public void setSuchergebnisse(final Suchergebnisse suchergebnisse) {
        this.suchergebnisse = suchergebnisse;
    }

    int getAnzahlSuchergebnisse() {
        return suchergebnisse.anzahl;
    }

    public static class Cron {

        private String expression;

        public void setExpression(final String expression) {
            this.expression = expression;
        }

    }

    public static class Wbh {

        private Gesamtdat gesamtdat = new Gesamtdat();

        public void setGesamtdat(final Gesamtdat gesamtdat) {
            this.gesamtdat = gesamtdat;
        }

        public static class Gesamtdat {

            private String filename;

            private Charset charset;

            public void setFilename(final String filename) {
                this.filename = filename;
            }

            public void setCharset(final Charset charset) {
                this.charset = charset;
            }

        }

    }

    public static class Suchergebnisse {

        private int anzahl;

        public void setAnzahl(final int anzahl) {
            this.anzahl = anzahl;
        }

    }

}

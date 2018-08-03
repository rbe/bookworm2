/*
 * eu.artofcoding.bookworm
 *
 * Copyright (C) 2011-2017 art of coding UG, http://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchkatalog.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import wbh.bookworm.hoerbuchkatalog.domain.Hoerbuch;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@Component
final class HoerbuchMapper {

    private static final String NEWLINE = "\r\n";

    private final RepositoryConfig repositoryConfig;

    private final HoerbuchFactory hoerbuchFactory;

    @Autowired
    public HoerbuchMapper(RepositoryConfig repositoryConfig, HoerbuchFactory hoerbuchFactory) {
        this.repositoryConfig = repositoryConfig;
        this.hoerbuchFactory = hoerbuchFactory;
    }

    /**
     * A line, terminated by CRLF
     */
    Set<Hoerbuch> parse(final String gesamtDat) {
        String[] lines = gesamtDat.split(NEWLINE);
        final Set<Hoerbuch> hoerbuecher = new HashSet<>();
        for (String line : lines) {
            final boolean lineIsEmpty = line.trim().isEmpty();
            if (!lineIsEmpty) {
                final Hoerbuch hoerbuch = hoerbuchFactory.fromGesamtDat(line);
                hoerbuecher.add(hoerbuch);
            }
        }
        return hoerbuecher;
    }

    Set<Hoerbuch> parse(final Path gesamtDat) throws IOException {
        final Charset charset = repositoryConfig.getWbhGesamtdatCharset();
        final Set<Hoerbuch> hoerbuecher;
        try (BufferedReader reader = Files.newBufferedReader(gesamtDat, charset)) {
            hoerbuecher = new HashSet<>();
            String line;
            while (null != (line = reader.readLine()) && line.trim().length() > 1) {
                final Hoerbuch hoerbuch = hoerbuchFactory.fromGesamtDat(line);
                hoerbuecher.add(hoerbuch);
            }
        }
        return null != hoerbuecher ? hoerbuecher : Collections.emptySet();
    }

}

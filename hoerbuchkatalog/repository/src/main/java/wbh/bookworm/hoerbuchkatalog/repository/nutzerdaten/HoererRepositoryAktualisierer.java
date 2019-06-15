/*
 * Copyright (C) 2011-2019 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchkatalog.repository.nutzerdaten;

import aoc.fs.DirectoryState;
import aoc.fs.FilesCompleteSpecification;
import aoc.fs.FilesystemWatcher;
import aoc.fs.SpecificationSatisfiedListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Component
public class HoererRepositoryAktualisierer {

    private static final Logger LOGGER = LoggerFactory.getLogger(HoererRepositoryAktualisierer.class);

    private final FilesystemWatcher filesystemWatcher;

    private final Set<Path> neededFiles = Set.of(
            Path.of("hoerstp.csv"),
            Path.of("hoekzstp.csv"),
            Path.of("hoebstp.csv")
    );

    @Autowired
    public HoererRepositoryAktualisierer(final ApplicationContext applicationContext,
                                         final TaskExecutor taskExecutor,
                                         final HoererRepositoryConfig hoererRepositoryConfig,
                                         final HoererRepository hoererRepository) {
        final Path verzeichnisAktualisierung = hoererRepositoryConfig.getDirectory()
                .resolve("../aktualisierung/eingangskorb");
        filesystemWatcher = applicationContext.getBean(FilesystemWatcher.class, verzeichnisAktualisierung);
        filesystemWatcher.register(new FilesCompleteSpecification(neededFiles, 30, TimeUnit.SECONDS),
                new DateienVerschiebenAktion(verzeichnisAktualisierung,
                        hoererRepositoryConfig.getDirectory(), hoererRepository));
        taskExecutor.execute(filesystemWatcher);
        LOGGER.info("Achte auf Dateien im Verzeichnis {}", verzeichnisAktualisierung);
    }

    @PreDestroy
    private void preDestroy() {
        filesystemWatcher.pleaseStop();
    }

    private class DateienVerschiebenAktion implements SpecificationSatisfiedListener {

        private final Path verzeichnisAktualisierung;

        private final Path directory;

        private final HoererRepository hoererRepository;

        private DateienVerschiebenAktion(final Path verzeichnisAktualisierung,
                                         final Path directory,
                                         final HoererRepository hoererRepository) {
            this.verzeichnisAktualisierung = verzeichnisAktualisierung;
            this.directory = directory;
            this.hoererRepository = hoererRepository;
        }

        @Override
        public void processSpecificationSatistied(final DirectoryState directoryState) {
            neededFiles.forEach(p -> {
                try {
                    Files.move(verzeichnisAktualisierung.resolve(p),
                            directory.resolve(p.getFileName()));
                } catch (IOException e) {
                    LOGGER.error("Kann Datei " + p + "  nicht nach " + directory
                            + " verschieben", e);
                }
            });
            hoererRepository.datenEinlesen();
        }

    }

}

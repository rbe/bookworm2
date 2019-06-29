/*
 * Copyright (C) 2011-2019 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package aoc.fs;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Set;

public final class FilesUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(FilesUtils.class);

    public static void move(final Set<Path> files, final Path from, final Path to) {
        files.forEach(p -> {
            try {
                Files.move(from.resolve(p), to.resolve(p.getFileName()),
                        StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e) {
                if (LOGGER.isErrorEnabled()) {
                    LOGGER.error("Could not move " + p + " to " + to, e);
                }
            }
        });
    }

}

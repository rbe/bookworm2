/*
 * Copyright (C) 2011-2019 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package aoc.fs;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileTime;
import java.time.Instant;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public final class FileState {

    private final Path file;

    FileState(final Path file) {
        Objects.requireNonNull(file);
        this.file = file;
    }

    public Path getFile() {
        return file;
    }

    public long getSize() {
        try {
            return Files.size(file);
        } catch (IOException e) {
            return -1L;
        }
    }

    public FileTime getLastModified() {
        try {
            return Files.getLastModifiedTime(file);
        } catch (IOException e) {
            return FileTime.fromMillis(-1L);
        }
    }

    public boolean isNotModifiedFor(final long timeout, final TimeUnit timeUnit) {
        FileTime fileTime;
        try {
            fileTime = Files.getLastModifiedTime(file);
        } catch (IOException e) {
            fileTime = null;
        }
        return null != fileTime &&
                Instant.now().minusMillis(timeUnit.toMillis(timeout))
                        .isAfter(fileTime.toInstant());
    }

    @Override
    public String toString() {
        return String.format("FileState{file=%s, size=%d, lastModified=%s}",
                file, getSize(), getLastModified());
    }

}

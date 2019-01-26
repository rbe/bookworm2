/*
 * Copyright (C) 2011-2019 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package aoc.tools.datatransfer;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public interface As400FileParser {

    String[] parseLine(String line);

    default List<String[]> parseLines(final Path path, final Charset charset,
                                      final int expectedLineCount) throws IOException {
        Objects.requireNonNull(path);
        Objects.requireNonNull(charset);
        final List<String[]> strings = new ArrayList<>(expectedLineCount);
        try (final BufferedReader reader = Files.newBufferedReader(path, charset)) {
            for (String line; null != (line = reader.readLine()); ) {
                strings.add(parseLine(line));
            }
        }
        return strings;
    }

    default <T> List<T> parseObjects(final Path path, final Charset charset,
                                     final int expectedLineCount,
                                     final Predicate<? super String[]> filter,
                                     final Function<? super String[], T> createObject) throws IOException {
        Objects.requireNonNull(filter);
        Objects.requireNonNull(createObject);
        return parseLines(path, charset, expectedLineCount)
                .parallelStream()
                .filter(filter)
                // TODO .map(this::encrypt)
                .map(createObject)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

}

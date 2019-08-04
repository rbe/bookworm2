/*
 * Copyright (C) 2011-2019 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package aoc.datatransfer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public interface LineFileParser {

    Logger LOGGER = LoggerFactory.getLogger(LineFileParser.class);

    String[] parseLine(String line);

    default Callable<List<String[]>> p(final Charset charset, final int expectedLineCount,
                                       final Path path) {
        return () -> {
            final LocalDateTime start = LocalDateTime.now();
            LOGGER.debug("Start parsing {} with charset {} and expected line count {}",
                    path, charset, expectedLineCount);
            try (final BufferedReader reader = Files.newBufferedReader(path, charset)) {
                final List<String[]> strings = new ArrayList<>(expectedLineCount);
                int lineno = 1;
                for (String line; null != (line = reader.readLine()); lineno++) {
                    try {
                        strings.add(parseLine(line));
                    } catch (Exception e) {
                        LOGGER.warn(path + ": Could not parse line #" + lineno, e);
                    }
                }
                LOGGER.debug("Parsed {} successfully in {}", path,
                        Duration.between(start, LocalDateTime.now()));
                return strings;
            } catch (Exception e) {
                LOGGER.error(path + ": Could not parse file", e);
                return Collections.emptyList();
            }
        };
    }

    default List<List<String[]>> parseLines(final Charset charset, final int expectedLineCount,
                                            final Path... paths) {
        Objects.requireNonNull(paths);
        Objects.requireNonNull(charset);
        // TODO Inject ExecutorService
        final int parallelism = Runtime.getRuntime().availableProcessors() / 2;
        final ExecutorService executorService = Executors.newWorkStealingPool(parallelism);
        final List<Future<List<String[]>>> futures = new ArrayList<>();
        final List<List<String[]>> pathStrings = new ArrayList<>(expectedLineCount);
        for (final Path path : paths) {
            futures.add(executorService.submit(
                    p(charset, expectedLineCount / paths.length, path)));
        }
        Objects.requireNonNull(futures).forEach(f -> {
            try {
                pathStrings.add(f.get());
            } catch (InterruptedException | ExecutionException e) {
                Thread.currentThread().interrupt();
            }
        });
        return pathStrings;
    }

    default List<String[]> flatParseLines(final Charset charset, final int expectedLineCount,
                                          final Path... path) {
        return parseLines(charset, expectedLineCount, path)
                .stream()
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }

    default <T> List<T> parseObjects(final Charset charset, final int expectedLineCount,
                                     final Path path,
                                     final Predicate<? super List<String[]>> filter,
                                     final Function<? super List<String[]>, T> createObject) {
        Objects.requireNonNull(filter);
        Objects.requireNonNull(createObject);
        return parseLines(charset, expectedLineCount, path)
                .parallelStream()
                .filter(filter)
                // TODO .map(this::encrypt)
                .map(createObject)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

}

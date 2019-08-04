/*
 * Copyright (C) 2011-2019 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package aoc.datatransfer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.AccessDeniedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

@Component
public final class FileSplitter {

    private static final Logger LOGGER = LoggerFactory.getLogger(FileSplitter.class);

    private final ExecutorService executorService;

    private ArrayBlockingQueue<String> queue;

    private Path[] splittedPaths;

    private ChunkWriter[] writer;

    private List<Future<?>> futures;

    private static class ChunkWriter implements Runnable {

        private static final Logger LOGGER = LoggerFactory.getLogger(ChunkWriter.class);

        private final BlockingQueue<String> queue;

        private final Path path;

        private final BufferedWriter stream;

        private boolean stopFlag;

        private ChunkWriter(final BlockingQueue<String> queue, final Path path) throws IOException {
            this.queue = queue;
            this.path = path;
            try {
                this.stream = Files.newBufferedWriter(path,
                        StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
            } catch (AccessDeniedException e) {
                LOGGER.error("Could not create file " + path.toAbsolutePath(), e);
                throw new IllegalStateException(e);
            }
        }

        public synchronized void stop() {
            stopFlag = true;
        }

        private int numLinesWritten;

        @Override
        public void run() {
            final LocalDateTime start = LocalDateTime.now();
            LOGGER.debug("Start writing lines from queue to {}", path);
            while (!stopFlag) {
                try {
                    final String item = queue.poll(100, TimeUnit.MILLISECONDS);
                    if (null != item) {
                        stream.write(item);
                        stream.newLine();
                        if (++numLinesWritten % 10_000 == 0 && LOGGER.isTraceEnabled()) {
                            LOGGER.trace("Wrote {} lines to {}", numLinesWritten, path);
                        }
                    }
                } catch (InterruptedException e) {
                    closeStream();
                    Thread.currentThread().interrupt();
                } catch (IOException e) {
                    LOGGER.error("", e);
                }
            }
            closeStream();
            LOGGER.debug("Finished, wrote {} lines to {} in {}",
                    numLinesWritten, path,
                    Duration.between(start, LocalDateTime.now()));
        }

        private void closeStream() {
            try {
                stream.flush();
                stream.close();
            } catch (IOException e) {
                LOGGER.error("", e);
            }
        }

    }

    @Autowired
    public FileSplitter(final ExecutorService executorService) {
        this.executorService = executorService;
    }

    private void setup(final Path path, final int expectedLineCount,
                       final int numWriters) throws IOException {
        final int capacity = expectedLineCount / numWriters;
        LOGGER.debug("Using queue with capacity {} ({} expected line count / {} writers)",
                capacity, expectedLineCount, numWriters);
        queue = new ArrayBlockingQueue<>(capacity);
        splittedPaths = new Path[numWriters];
        writer = new ChunkWriter[numWriters];
        futures = new ArrayList<>(numWriters);
        for (int i = 0; i < numWriters; i++) {
            final Path directory = path.getParent();
            splittedPaths[i] = directory.resolve(path.getFileName().toString() + "_" + i);
            writer[i] = new ChunkWriter(queue, splittedPaths[i]);
            futures.add(executorService.submit(writer[i]));
        }
        LOGGER.debug("Set up for splitting {} with expected number of lines {} and {} writers",
                path, expectedLineCount, numWriters);
    }

    private void tearDown(int numWriters) {
        while (!queue.isEmpty()) {
            try {
                int time = 250;
                LOGGER.debug("Queue has {} items left, witing {} milliseconds for writers to finish",
                        queue.size(), time);
                TimeUnit.MILLISECONDS.sleep(time);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        for (int i = 0; i < numWriters; i++) {
            writer[i].stop();
        }
        futures.forEach(f -> {
            try {
                f.get();
            } catch (InterruptedException | ExecutionException e) {
                Thread.currentThread().interrupt();
            }
        });
    }

    public Path[] split(final Path path, final Charset charset,
                        int expectedLineCount, int numWriters) throws IOException {
        final LocalDateTime start = LocalDateTime.now();
        try (final BufferedReader reader = Files.newBufferedReader(path, charset)) {
            setup(path, expectedLineCount, numWriters);
            String line;
            while (null != (line = reader.readLine())) {
                try {
                    queue.put(line);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
            tearDown(numWriters);
        }
        // TODO Just return splittedPaths with content
        LOGGER.debug("Splitted {} into {} segments {} in {}",
                path, numWriters, Arrays.asList(splittedPaths),
                Duration.between(start, LocalDateTime.now()));
        return splittedPaths;
    }

}

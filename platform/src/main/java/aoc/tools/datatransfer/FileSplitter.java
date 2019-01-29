/*
 * Copyright (C) 2011-2019 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package aoc.tools.datatransfer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

public final class FileSplitter {

    private static final Logger LOGGER = LoggerFactory.getLogger(FileSplitter.class);

    private static class MyWriter implements Runnable {

        private static final Logger LOGGER = LoggerFactory.getLogger(MyWriter.class);

        private final BlockingQueue<String> queue;

        private final Path path;

        private final BufferedWriter stream;

        private boolean stopFlag;

        private MyWriter(final BlockingQueue<String> queue, final Path path) throws IOException {
            this.queue = queue;
            this.path = path;
            this.stream = Files.newBufferedWriter(path,
                    StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        }

        public synchronized void stop() {
            stopFlag = true;
        }

        private int numLinesWritten;

        @Override
        public void run() {
            final LocalDateTime start = LocalDateTime.now();
            LOGGER.debug("Start writing to {}", path);
            while (!stopFlag) {
                try {
                    final String item = queue.poll(100, TimeUnit.MILLISECONDS);
                    if (null != item) {
                        stream.write(item);
                        stream.newLine();
                        if (++numLinesWritten % 100_000 == 0) {
                            LOGGER.trace("Wrote {} lines", numLinesWritten);
                        }
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } catch (IOException e) {
                    LOGGER.error("", e);
                }
            }
            try {
                stream.flush();
                stream.close();
            } catch (IOException e) {
                LOGGER.error("", e);
            }
            LOGGER.debug("Finished, wrote {} lines in {}", numLinesWritten,
                    Duration.between(start, LocalDateTime.now()));
        }

    }

    private FileSplitter() {
        throw new AssertionError();
    }

    public static Path[] split(final Path path, final Charset charset, int numWriters) throws IOException {
        final Path[] splittedPaths = new Path[numWriters];
        final LocalDateTime start = LocalDateTime.now();
        try (final BufferedReader reader = Files.newBufferedReader(path, charset)) {
            final ArrayBlockingQueue<String> queue = new ArrayBlockingQueue<>(2_900_000);
            final MyWriter[] writer = new MyWriter[numWriters];
            for (int i = 0; i < numWriters; i++) {
                splittedPaths[i] = Path.of(path.getFileName().toString() + "_" + i);
                writer[i] = new MyWriter(queue, splittedPaths[i]);
                new Thread(writer[i]).start();
            }
            String line;
            while (null != (line = reader.readLine())) {
                if (!queue.offer(line)) {
                    throw new IllegalStateException("offer()==false");
                }
            }
            while (!queue.isEmpty()) {
                try {
                    LOGGER.debug("Waiting 1 seconds for writers to finish");
                    TimeUnit.SECONDS.sleep(1);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
            for (int i = 0; i < numWriters; i++) {
                writer[i].stop();
            }
        }
        LOGGER.debug("Splitting {} into {} segments took {}",
                path, numWriters,
                Duration.between(start, LocalDateTime.now()));
        return splittedPaths;
    }

}

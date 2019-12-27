/*
 * Copyright (C) 2011-2019 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package aoc.dataformat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;

public final class Executor {

    private static final Logger LOGGER = LoggerFactory.getLogger(Executor.class);

    private Executor() {
        throw new AssertionError();
    }

    public static <T> List<T> invokeAllAndGet(final ExecutorService executorService,
                                              final Collection<Callable<T>> runnables) {
        final List<T> list = new ArrayList<>();
        try {
            executorService.invokeAll(runnables).forEach(f -> {
                try {
                    list.add(f.get());
                } catch (InterruptedException | ExecutionException e) {
                    LOGGER.warn("Thread interrupted or got exception while executing background thread", e);
                    Thread.currentThread().interrupt();
                }
            });
        } catch (InterruptedException e) {
            LOGGER.warn("Interrupted", e);
            Thread.currentThread().interrupt();
        }
        return list;
    }

}

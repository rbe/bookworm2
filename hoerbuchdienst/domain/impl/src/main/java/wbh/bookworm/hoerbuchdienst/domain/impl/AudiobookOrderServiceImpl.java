/*
 * Copyright (C) 2011-2020 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchdienst.domain.impl;

import javax.inject.Singleton;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import io.micronaut.context.annotation.Value;
import io.micronaut.scheduling.annotation.Async;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import wbh.bookworm.hoerbuchdienst.domain.ports.AudiobookOrderService;
import wbh.bookworm.hoerbuchdienst.domain.ports.AudiobookServiceException;

import aoc.mikrokosmos.io.fs.FilesUtils;

@Singleton
class AudiobookOrderServiceImpl implements AudiobookOrderService {

    private static final Logger LOGGER = LoggerFactory.getLogger(AudiobookOrderServiceImpl.class);

    private static final String DAISY_ZIP = "DAISY.zip";

    private static final String ORDER_STATUS_PROCESSING = "PROCESSING";

    private static final String ORDER_STATUS_SUCCESS = "SUCCESS";

    private static final String ORDER_STATUS_FAILED = "FAILED";

    private static final String ORDER_STATUS_UNKNOWN = "UNKNOWN";

    private final Map<String, String> orderStatus;

    private final AudiobookZipper audiobookZipper;

    @Value("${hoerbuchdienst.temporary.path}")
    private Path temporaryDirectory;

    AudiobookOrderServiceImpl(final AudiobookZipper audiobookZipper) {
        this.audiobookZipper = audiobookZipper;
        orderStatus = new ConcurrentHashMap<>(10);
    }

    @Override
    @Async
    public void orderZip(final String mandant, final String hoerernummer, /* TODO Mandantenspezifisch */ final String titelnummer, final String orderId) {
        LOGGER.debug("Hörer '{}' Hörbuch '{}': Erstelle Hörbuch mit Wasserzeichen als ZIP",
                hoerernummer, titelnummer);
        orderStatus.put(orderId, ORDER_STATUS_PROCESSING);
        final Path zipFile = audiobookZipper.watermarkedAudiobookAsZip(mandant, hoerernummer, titelnummer);
        try (final InputStream audiobook = Files.newInputStream(zipFile)) {
            LOGGER.info("Hörer '{}' Hörbuch '{}': Hörbuch mit Wasserzeichen als ZIP erstellt",
                    hoerernummer, titelnummer);
            final Path orderDirectory = temporaryDirectory.resolve(orderId);
            Files.createDirectories(orderDirectory);
            Files.write(orderDirectory.resolve(DAISY_ZIP), audiobook.readAllBytes());
            orderStatus.put(orderId, ORDER_STATUS_SUCCESS);
        } catch (Exception e) {
            orderStatus.put(orderId, ORDER_STATUS_FAILED);
            throw new AudiobookServiceException(String.format("Hörer %s Hörbuch %s: Kann Bestellung nicht persistieren", hoerernummer, titelnummer), e);
        }
    }

    @Override
    public String orderStatus(final String orderId) {
        return orderStatus.getOrDefault(orderId, ORDER_STATUS_UNKNOWN);
    }

    @Override
    public InputStream fetchOrder(final String orderId) {
        try {
            final Path orderDirectory = temporaryDirectory.resolve(orderId);
            final InputStream inputStream = Files.newInputStream(orderDirectory.resolve(DAISY_ZIP));
            orderStatus.remove(orderId);
            FilesUtils.cleanupTemporaryDirectory(orderDirectory);
            return inputStream;
        } catch (IOException e) {
            throw new AudiobookServiceException(String.format("Bestellung %s kann nicht abgerufen werden", orderId), e);
        }
    }

}

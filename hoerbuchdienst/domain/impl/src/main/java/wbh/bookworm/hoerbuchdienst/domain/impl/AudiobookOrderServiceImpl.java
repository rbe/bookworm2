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
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import io.micronaut.context.annotation.Value;
import io.micronaut.scheduling.annotation.Async;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import wbh.bookworm.hoerbuchdienst.domain.ports.AudiobookOrderService;

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
        LOGGER.debug("Hörer '{}' Hörbuch '{}' Bestellung {}: Erstelle DAISY ZIP", hoerernummer, titelnummer, orderId);
        orderStatus.put(orderId, ORDER_STATUS_PROCESSING);
        try {
            final Path zipFile = audiobookZipper.watermarkedDaisyZipAsFile(mandant, hoerernummer, titelnummer);
            if (null != zipFile) {
                LOGGER.info("Hörer '{}' Hörbuch '{}' Bestellung {}: DAISY ZIP erstellt", hoerernummer, titelnummer, orderId);
                final Path orderDirectory = temporaryDirectory.resolve(orderId);
                Files.createDirectories(orderDirectory);
                final Path daisyZip = orderDirectory.resolve(DAISY_ZIP);
                Files.move(zipFile, daisyZip);
                orderStatus.put(orderId, ORDER_STATUS_SUCCESS);
                LOGGER.info("Hörer '{}' Hörbuch '{}' Bestellung '{}': DAISY ZIP unter '{}' bereitgestellt", hoerernummer, titelnummer, orderId, daisyZip);
            } else {
                orderStatus.put(orderId, ORDER_STATUS_FAILED);
                LOGGER.error("Hörer '{}' Hörbuch '{}' Bestellung '{}': Fehler bei der Bereitstellung des DAISY ZIP", hoerernummer, titelnummer, orderId);
            }
        } catch (Exception e) {
            orderStatus.put(orderId, ORDER_STATUS_FAILED);
            LOGGER.error(String.format("Hörer '%s' Hörbuch '%s' Bestellung '%s': Kann Bestellung nicht verarbeiten",
                    hoerernummer, titelnummer, orderId), e);
        }
    }

    @Override
    public String orderStatus(final String orderId) {
        return orderStatus.getOrDefault(orderId, ORDER_STATUS_UNKNOWN);
    }

    @Override
    public Optional<InputStream> fetchOrderAsStream(final String orderId) {
        final Path daisyZip = fetchOrderAsFile(orderId).orElseThrow();
        try {
            return Optional.of(Files.newInputStream(daisyZip));
        } catch (IOException e) {
            LOGGER.error("", e);
            return Optional.empty();
        }
    }

    @Override
    public Optional<Path> fetchOrderAsFile(final String orderId) {
        final Path daisyZip = temporaryDirectory.resolve(orderId).resolve(DAISY_ZIP);
        final boolean orderExists = orderStatus.containsKey(orderId)
                && Files.exists(daisyZip);
        if (orderExists) {
            LOGGER.info("Bestellung {} wird abgeholt", orderId);
            orderStatus.remove(orderId);
            return Optional.of(daisyZip);
        } else {
            LOGGER.warn("Bestellung {} existiert nicht", orderId);
            return Optional.empty();
        }
    }

    @Override
    public Optional<Long> fileSize(final String orderId) {
        final Path daisyZip = temporaryDirectory.resolve(orderId).resolve(DAISY_ZIP);
        final boolean orderExists = orderStatus.containsKey(orderId)
                && Files.exists(daisyZip);
        if (orderExists) {
            try {
                return Optional.of(Files.size(daisyZip));
            } catch (IOException e) {
                LOGGER.error(String.format("Kann Größe des DASY ZIPs %s nicht bestimmen", orderId), e);
                return Optional.of(-1L);
            }
        } else {
            return Optional.empty();
        }
    }

}

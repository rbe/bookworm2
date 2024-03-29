/*
 * Copyright (C) 2011-2020 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchdienst.domain.impl;

import javax.inject.Singleton;
import java.io.IOException;
import java.io.InputStream;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
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
import wbh.bookworm.hoerbuchdienst.domain.required.hoerbuchkatalog.HoerbuchkatalogClient;
import wbh.bookworm.shared.domain.Hoerernummer;

import aoc.mikrokosmos.io.fs.DeleteOnCloseSeekableByteChannel;

@Singleton
class AudiobookOrderServiceImpl implements AudiobookOrderService {

    private static final Logger LOGGER = LoggerFactory.getLogger(AudiobookOrderServiceImpl.class);

    private static final String ORDER_STATUS_PROCESSING = "PROCESSING";

    private static final String ORDER_STATUS_SUCCESS = "SUCCESS";

    private static final String ORDER_STATUS_FAILED = "FAILED";

    private static final String ORDER_STATUS_UNKNOWN = "UNKNOWN";

    private final Map<String, String> orderStatus;

    private final AudiobookZipper audiobookZipper;

    private final Map<String, Hoerernummer> bestellungen;

    private final HoerbuchkatalogClient hoerbuchkatalogClient;

    @Value("${hoerbuchdienst.temporary.path}")
    private Path temporaryDirectory;

    AudiobookOrderServiceImpl(final AudiobookZipper audiobookZipper,
                              final HoerbuchkatalogClient hoerbuchkatalogClient) {
        this.audiobookZipper = audiobookZipper;
        this.hoerbuchkatalogClient = hoerbuchkatalogClient;
        orderStatus = new ConcurrentHashMap<>(10);
        bestellungen = new ConcurrentHashMap<>();
    }

    @Override
    @Async
    public void orderZip(final String mandant, final String hoerernummer, /* TODO Mandantenspezifisch */ final String titelnummer, final String orderId) {
        LOGGER.debug("Hörer '{}' Hörbuch '{}' Bestellung {}: Erstelle DAISY ZIP", hoerernummer, titelnummer, orderId);
        orderStatus.put(orderId, ORDER_STATUS_PROCESSING);
        bestellungen.put(orderId, new Hoerernummer(hoerernummer));
        try {
            final Path zipFile = audiobookZipper.watermarkedDaisyZipAsFile(mandant, hoerernummer, titelnummer);
            if (null != zipFile) {
                LOGGER.info("Hörer '{}' Hörbuch '{}' Bestellung {}: DAISY ZIP erstellt", hoerernummer, titelnummer, orderId);
                final Path daisyZip = daisyZipPath(orderId);
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
    public Optional<InputStream> fetchOrderAsStream(final String orderId, final String titelnummer) {
        final Optional<Path> maybeDaisyZip = fetchOrderAsFile(orderId);
        if (maybeDaisyZip.isPresent()) {
            LOGGER.info("Bestellung '{}' wird abgeholt", orderId);
            if (bestellungen.containsKey(orderId)) {
                try {
                    final Hoerernummer hoerernummer = bestellungen.get(orderId);
                    hoerbuchkatalogClient.verbuche(hoerernummer.getValue(), titelnummer);
                } catch (Exception e) {
                    LOGGER.error("", e);
                }
            } else {
                LOGGER.warn("Bestellung {} hat keine Hörernummer, Download nicht verbucht", orderId);
            }
            try {
                final ReadableByteChannel byteChannel = new DeleteOnCloseSeekableByteChannel(maybeDaisyZip.get());
                return Optional.of(Channels.newInputStream(byteChannel));
            } catch (IOException e) {
                LOGGER.error(String.format("Bestellung '%s'", orderId), e);
            }
        } else {
            LOGGER.error("Bestellung '{}' existiert nicht", orderId);
        }
        return Optional.empty();
    }

    @Override
    public Optional<Path> fetchOrderAsFile(final String orderId) {
        final Path daisyZip = daisyZipPath(orderId);
        final boolean orderExists = orderStatus.containsKey(orderId)
                && Files.exists(daisyZip);
        if (orderExists) {
            orderStatus.remove(orderId);
            return Optional.of(daisyZip);
        } else {
            return Optional.empty();
        }
    }

    @Override
    public Optional<Long> fileSize(final String orderId) {
        final Path daisyZip = daisyZipPath(orderId);
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

    @Override
    public Optional<Long> lastModified(final String orderId) {
        final Path daisyZip = daisyZipPath(orderId);
        final boolean orderExists = orderStatus.containsKey(orderId)
                && Files.exists(daisyZip);
        if (orderExists) {
            try {
                return Optional.of(Files.getLastModifiedTime(daisyZip).toMillis());
            } catch (IOException e) {
                LOGGER.error(String.format("Kann letzten Modifizierungszeitpunkt des DASY ZIPs %s nicht bestimmen", orderId), e);
                return Optional.of(-1L);
            }
        } else {
            return Optional.empty();
        }
    }

    private Path daisyZipPath(final String orderId) {
        return temporaryDirectory.resolve(orderId + ".zip");
    }

}

/*
 * Copyright (C) 2011-2020 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchdienst.domain.impl;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

import io.micronaut.context.annotation.Value;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import wbh.bookworm.hoerbuchdienst.domain.ports.AudiobookServiceException;
import wbh.bookworm.hoerbuchdienst.domain.ports.AudiobookStreamService;
import wbh.bookworm.hoerbuchdienst.domain.required.audiobookrepository.AudiobookRepository;
import wbh.bookworm.hoerbuchdienst.domain.required.watermark.Watermarker;

import aoc.mikrokosmos.io.fs.FilesUtils;

@Singleton
class AudiobookStreamServiceImpl implements AudiobookStreamService {

    private static final Logger LOGGER = LoggerFactory.getLogger(AudiobookStreamServiceImpl.class);

    private final AudiobookRepository audiobookRepository;

    private final Watermarker watermarker;

    private final AudiobookZipper audiobookZipper;

    @Value(/* TODO Mandantenspezifisch */"${hoerbuchdienst.piracy.inquiry.urlprefix}")
    private String piracyInquiryUrlPrefix;

    @Inject
    AudiobookStreamServiceImpl(final AudiobookRepository audiobookRepository,
                               final Watermarker watermarker,
                               final AudiobookZipper audiobookZipper) {
        this.audiobookRepository = audiobookRepository;
        this.watermarker = watermarker;
        this.audiobookZipper = audiobookZipper;
    }

    @Override
    public InputStream trackAsStream(final String mandant, final String hoerernummer, final String titelnummer, final String ident) {
        final Path tempMp3File = audiobookRepository.trackAsFile(hoerernummer, titelnummer, ident);
        try {
            final String watermark = watermarker.makeWatermark(mandant, hoerernummer, titelnummer);
            watermarker.addWatermarkInPlace(watermark, piracyInquiryUrlPrefix, tempMp3File);
            final byte[] watermarkedMp3Track = Files.readAllBytes(tempMp3File);
            FilesUtils.tryDelete(tempMp3File);
            return new ByteArrayInputStream(watermarkedMp3Track);
        } catch (IOException e) {
            throw new AudiobookServiceException("", e);
        }
    }

    @Override
    public InputStream zipAsStream(final String mandant, final String hoerernummer, final String titelnummer) {
        return audiobookZipper.watermarkedDaisyZipAsStream(mandant, hoerernummer, titelnummer);
    }

}

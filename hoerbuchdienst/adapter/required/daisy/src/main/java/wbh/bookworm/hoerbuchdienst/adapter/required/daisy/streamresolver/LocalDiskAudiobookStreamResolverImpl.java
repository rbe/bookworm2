/*
 * Copyright (C) 2011-2020 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchdienst.adapter.required.daisy.streamresolver;

import javax.annotation.PostConstruct;
import javax.inject.Named;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import io.micronaut.context.annotation.Property;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import aoc.mikrokosmos.objectstorage.api.ObjectMetaInfo;

@Named("localdisk")
class LocalDiskAudiobookStreamResolverImpl implements AudiobookStreamResolver {

    private static final Logger LOGGER = LoggerFactory.getLogger(LocalDiskAudiobookStreamResolverImpl.class);

    @Property(name = ResolverConfigurationKeys.HOERBUCHDIENST_REPOSITORY_LOCALDISK_URI)
    private Path audiobookDirectory;

    LocalDiskAudiobookStreamResolverImpl() {
    }

    LocalDiskAudiobookStreamResolverImpl(final Path audiobookDirectory) {
        this.audiobookDirectory = audiobookDirectory;
    }

    @PostConstruct
    private void postConstruct() {
        LOGGER.debug("This is {}", this);
    }

    @Override
    public List<Path> listAll() {
        final List<Path> list = new ArrayList<>();
        try (final Stream<Path> paths = Files.walk(audiobookDirectory.resolve("."), 1)) {
            // TODO "Kapitel" Suffix ist mandantenspezifisch
            list.addAll(paths.filter(p -> p.getFileName().toString().endsWith("Kapitel"))
                    .collect(Collectors.toUnmodifiableList()));
        } catch (IOException e) {
            throw new AudiobookStreamResolverException("", e);
        }
        return list;
    }

    @Override
    public List<ObjectMetaInfo> allObjectsMetaInfo() {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<Path> list(final String titelnummer) {
        final List<Path> list = new ArrayList<>();
        try (final Stream<Path> paths = Files.walk(audiobookDirectory
                .resolve(titelnummer).resolve("."), 1)) {
            // TODO "Kapitel" Suffix ist mandantenspezifisch
            list.addAll(paths.filter(p -> p.getFileName().toString().endsWith("Kapitel"))
                    .collect(Collectors.toUnmodifiableList()));
        } catch (IOException e) {
            throw new AudiobookStreamResolverException("", e);
        }
        return list;
    }

    @Override
    public InputStream nccHtmlStream(final String titelnummer) {
        try {
            return Files.newInputStream(audiobookDirectory
                    // TODO "Kapitel" Suffix ist mandantenspezifisch
                    .resolve(String.format("%sKapitel", titelnummer))
                    .resolve("ncc.html"));
        } catch (IOException e) {
            throw new AudiobookStreamResolverException("", e);
        }
    }

    @Override
    public InputStream masterSmilStream(final String titelnummer) {
        try {
            return Files.newInputStream(audiobookDirectory
                    // TODO "Kapitel" Suffix ist mandantenspezifisch
                    .resolve(String.format("%sKapitel", titelnummer))
                    .resolve("master.smil"));
        } catch (IOException e) {
            throw new AudiobookStreamResolverException("", e);
        }
    }

    @Override
    public InputStream trackAsStream(final String titelnummer, final String ident) {
        try {
            return Files.newInputStream(audiobookDirectory
                    // TODO "Kapitel" Suffix ist mandantenspezifisch
                    .resolve(String.format("%sKapitel", titelnummer))
                    .resolve(ident));
        } catch (IOException e) {
            throw new AudiobookStreamResolverException("", e);
        }
    }

    @Override
    public InputStream zipAsStream(final String titelnummer) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String putZip(final InputStream inputStream, final String titelnummer) {
        throw new UnsupportedOperationException();
    }

}

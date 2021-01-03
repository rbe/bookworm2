/*
 * Copyright (C) 2011-2020 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchdienst.domain.required.audiobookrepository;

import java.io.InputStream;
import java.nio.file.Path;
import java.util.List;

import wbh.bookworm.shared.domain.Titelnummer;

public interface AudiobookRepository {

    List</* TODO Mandantenspezifisch */Titelnummer> allEntriesByKey();

    Audiobook find(/* TODO Mandantenspezifisch */String titelnummer);

    Path trackAsFile(String hoerernummer, /* TODO Mandantenspezifisch */String titelnummer, String ident);

    InputStream trackAsStream(/*TODO Titelnummer*/String titelnummer, String ident);

    InputStream zipAsStream(/* TODO Mandantenspezifisch */String titelnummer);

    Path mp3ToTempDirectory(String titelnummer, Path tempDirectory);

    List<Path> findFilenames(String titelnummer);

}

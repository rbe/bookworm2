/*
 * Copyright (C) 2011-2020 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchdienst.domain.required.audiobookrepository;

import java.io.InputStream;
import java.nio.file.Path;
import java.util.List;

import wbh.bookworm.shared.domain.hoerbuch.Titelnummer;

public interface AudiobookRepository {

    List</* TODO AghNummer */Titelnummer> allEntriesByKey();

    Audiobook find(/* TODO AghNummer */String titelnummer);

    Path trackAsFile(String hoerernummer,
            /* TODO AghNummer */String titelnummer, String ident,
                     String temporaryId);

    InputStream trackAsStream(/*TODO Titelnummer*/String titelnummer, String ident);

    InputStream zipAsStream(/* TODO AghNummer */String titelnummer);

}

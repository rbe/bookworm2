/*
 * Copyright (C) 2011-2020 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchdienst.domain.required.audiobookobjectstorage;

import java.io.InputStream;

import wbh.bookworm.hoerbuchdienst.domain.required.audiobook.Audiobook;

public interface AudiobookObjectStorage {

    boolean exists(String titlenummer);

    Audiobook info(String titelnummer);

    InputStream trackAsStream(String titelnummer, String ident);

    InputStream asZip(String titelnummer);

}

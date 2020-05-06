/*
 * Copyright (C) 2011-2020 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchdienst.domain.required.audiobookrepository;

import java.io.InputStream;

public interface AudiobookRepository {

    /*TODO Titelnummer*/String[] findAll(String[] keywords);

    Audiobook find(/*TODO Titelnummer*/String titelnummer);

    InputStream trackAsStream(/*TODO Titelnummer*/String titelnummer, String ident);

    InputStream zipAsStream(String titelnummer);

}

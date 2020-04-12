/*
 * Copyright (C) 2011-2020 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchdienst.domain.required.audiobook;

public interface AudiobookRepository {

    Audiobook find(String titelnummer);

    /*TODO Titelnummer*/String[] findAll(String keyword);

    byte[] read(/*TODO Titelnummer*/String titelnummer, String ident);

    boolean index();

}

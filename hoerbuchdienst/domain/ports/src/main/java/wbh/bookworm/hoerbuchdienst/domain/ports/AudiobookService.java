/*
 * Copyright (C) 2011-2020 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchdienst.domain.ports;

import java.io.InputStream;

public interface AudiobookService {

    TrackInfoDTO trackInfo(String hoerernummer, String titelnummer, String ident);

    InputStream trackAsStream(String hoerernummer, String titelnummer, String ident);

    InputStream zipAsStream(String hoerernummer, String titelnummer);

}

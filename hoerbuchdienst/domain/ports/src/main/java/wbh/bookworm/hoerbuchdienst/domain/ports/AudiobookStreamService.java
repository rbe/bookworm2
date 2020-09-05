/*
 * Copyright (C) 2011-2020 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchdienst.domain.ports;

import java.io.InputStream;

public interface AudiobookStreamService {

    InputStream trackAsStream(String mandant, String hoerernummer, /* TODO Mandantenspezifisch */String titelnummer, String ident);

    InputStream zipAsStream(String mandant, String hoerernummer, /* TODO Mandantenspezifisch */String titelnummer);

}

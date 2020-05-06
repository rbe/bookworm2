/*
 * Copyright (C) 2011-2020 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchdienst.adapter.required.daisyaudiobook;

import java.io.InputStream;
import java.nio.file.Path;
import java.util.List;

interface AudiobookStreamResolver {

    List<Path> listAll();

    List<Path> list(/* TODO AGH Nummer o.ä. */String titelnummer);

    InputStream nccHtmlStream(/* TODO AGH Nummer o.ä. */String titelnummer);

    InputStream masterSmilStream(/* TODO AGH Nummer o.ä. */String titelnummer);

    InputStream trackAsStream(/* TODO AGH Nummer o.ä. */String titelnummer, String ident);

    InputStream zipAsStream(/* TODO AGH Nummer o.ä. */String titelnummer);

}

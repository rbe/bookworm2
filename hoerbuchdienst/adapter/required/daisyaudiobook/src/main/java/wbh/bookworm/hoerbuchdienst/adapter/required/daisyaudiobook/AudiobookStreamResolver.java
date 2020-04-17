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

    List<Path> list(String titelnummer);

    InputStream nccHtmlStream(String titelnummer);

    InputStream masterSmilStream(String titelnummer);

    InputStream trackAsStream(String titelnummer, String ident);

}

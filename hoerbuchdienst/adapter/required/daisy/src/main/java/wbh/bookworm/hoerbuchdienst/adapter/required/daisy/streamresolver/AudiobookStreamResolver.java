/*
 * Copyright (C) 2011-2020 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchdienst.adapter.required.daisy.streamresolver;

import java.io.InputStream;
import java.nio.file.Path;
import java.util.List;

import aoc.mikrokosmos.objectstorage.api.ObjectMetaInfo;

public interface AudiobookStreamResolver {

    List<Path> listAll();

    List<ObjectMetaInfo> objectsMetaInfo();

    List<Path> list(/*TODO Mandantenspezifisch*/String titelnummer);

    InputStream nccHtmlStream(/*TODO Mandantenspezifisch*/String titelnummer);

    InputStream masterSmilStream(/*TODO Mandantenspezifisch*/String titelnummer);

    InputStream trackAsStream(/*TODO Mandantenspezifisch*/String titelnummer, String ident);

    InputStream zipAsStream(/*TODO Mandantenspezifisch*/String titelnummer);

    String putZip(byte[] bytes, /*TODO Mandantenspezifisch*/String titelnummer);

    String putZip(InputStream inputStream, /*TODO Mandantenspezifisch*/String titelnummer);

    void removeZip(String titelnummer);

    Path mp3ToTempDirectory(String titelnummer, Path tempDirectory);

}

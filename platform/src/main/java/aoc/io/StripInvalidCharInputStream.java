/*
 * Copyright (C) 2011-2019 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package aoc.io;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

public class StripInvalidCharInputStream extends FilterInputStream {

    private static final Logger LOGGER = LoggerFactory.getLogger(StripInvalidCharInputStream.class);

    public StripInvalidCharInputStream(InputStream in) {
        super(in);
    }

    private static boolean isInValid(byte c) {
        return (c == '&'
                /*|| (c >= 0x00 && c <= 0x08)
                || (c >= 0x0b && c <= 0x0c)
                || (c >= 0x0e && c <= 0x1f)*/);
    }

    @Override
    public int read(byte[] b) throws IOException {
        int read = super.read(b);
        if (read == -1) {
            return -1;
        }
        filter(b, 0, b.length);
        return read;
    }

    @Override
    public int read(byte[] cbuf, int off, int len) throws IOException {
        int read = super.read(cbuf, off, len);
        if (read == -1) {
            return -1;
        }
        filter(cbuf, off, len);
        return read;
    }

    private void filter(byte[] arr, int off, int len) {
        for (int i = off; i < len; i++) {
            if (isInValid(arr[i])) {
                LOGGER.info("found control character: '{}'", (char) arr[i]);
                arr[i] = (byte) '_';
            }
        }
    }

    // TODO Test
    public static void main(String[] args) throws Exception {
        final InputStream in = Files.newInputStream(
                Path.of("/Users/rbe/project/wbh.bookworm/hoerbuchkatalog/infrastructure/src/main/java/wbh/bookworm/hoerbuchkatalog/infrastructure/blista/restdlskatalog/xml-defekt-ampersand.xml"),
                StandardOpenOption.READ);
        byte[] b = new StripInvalidCharInputStream(in).readAllBytes();
        System.out.println(new String(b));
    }

}

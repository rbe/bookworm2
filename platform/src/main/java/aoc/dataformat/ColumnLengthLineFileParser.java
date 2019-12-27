/*
 * Copyright (C) 2011-2019 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package aoc.dataformat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;

public final class ColumnLengthLineFileParser implements LineFileParser {

    private static final Logger LOGGER = LoggerFactory.getLogger(ColumnLengthLineFileParser.class);

    private final int[] columnLengths;

    public ColumnLengthLineFileParser(final int[] columnLengths) {
        this.columnLengths = Arrays.copyOf(columnLengths, columnLengths.length);
    }

    @Override
    public String[] parseLine(final String line) {
        LOGGER.trace("Parsing line: {}", line);
        final int sum = Arrays.stream(columnLengths).sum();
        if (line.length() >= sum) {
            final String[] arr = new String[columnLengths.length];
            int fromIndex = 0;
            int length;
            for (int cp = 0, size = columnLengths.length; cp < size; cp++) {
                length = columnLengths[cp];
                final String substring = line.substring(fromIndex, fromIndex + length);
                arr[cp] = substring.trim();
                fromIndex += length;
            }
            LOGGER.trace("Parsed line into '{}' elements: {}", arr.length, arr);
            return arr;
        } else {
            LOGGER.warn("Cannot parse line: '{}'", line);
            return new String[0];
        }
    }

}

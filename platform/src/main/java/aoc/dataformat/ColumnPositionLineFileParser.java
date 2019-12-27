/*
 * Copyright (C) 2011-2019 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package aoc.dataformat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;

public final class ColumnPositionLineFileParser implements LineFileParser {

    private static final Logger LOGGER = LoggerFactory.getLogger(ColumnPositionLineFileParser.class);

    private final int[] columnPositions;

    public ColumnPositionLineFileParser(final int[] columnPositions) {
        this.columnPositions = Arrays.copyOf(columnPositions, columnPositions.length);
    }

    @Override
    public String[] parseLine(final String line) {
        LOGGER.trace("Parsing line: {}", line);
        if (line.length() >= columnPositions[columnPositions.length - 1]) {
            final String[] arr = new String[columnPositions.length];
            for (int cp = 0, size = columnPositions.length; cp < size; cp++) {
                int fromIndex = columnPositions[cp];
                int toIndex;
                if (cp == size - 1) {
                    toIndex = line.length();
                } else {
                    toIndex = columnPositions[cp + 1];
                }
                final String substring = line.substring(fromIndex, toIndex);
                arr[cp] = substring.trim();
            }
            LOGGER.trace("Parsed line into '{}' elements: {}", arr.length, arr);
            return arr;
        } else {
            LOGGER.warn("Cannot parse line: '{}'", line);
            return new String[0];
        }
    }

}

/*
 * Copyright (C) 2011-2019 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package aoc.tools.datatransfer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public final class CsvFormat {

    private static final Logger LOGGER = LoggerFactory.getLogger(CsvFormat.class);

    private final List<CsvField> csvFields;

    private int index;

    public CsvFormat() {
        csvFields = new ArrayList<>();
        index = 0;
    }

    public void addField(final String name, final String description, final int index) {
        LOGGER.debug("Adding field {}/{} as index {}", name, description, index);
        csvFields.add(new CsvField(name, description, index));
        if (index > this.index) {
            this.index = index;
        }
    }

    public void addField(final String name, final String description) {
        LOGGER.debug("Adding field {}/{} as index {}", name, description, index);
        csvFields.add(new CsvField(name, description, index));
        index++;
    }

    public Optional<CsvField> fieldAt(final int index) {
        return csvFields.parallelStream()
                .filter(f -> f.index == index)
                .findFirst();
    }

    public Optional<CsvField> findField(final String str) {
        return csvFields.parallelStream()
                .filter(f -> f.name.equalsIgnoreCase(str) || f.description.equalsIgnoreCase(str))
                .findFirst();
    }

    public static final class CsvField {

        private final String name;

        private final String description;

        private final int index;

        private CsvField(final String name, final String description, final int index) {
            this.name = name;
            this.description = description;
            this.index = index;
        }

        public String getName() {
            return name;
        }

        public String getDescription() {
            return description;
        }

        public int getIndex() {
            return index;
        }

    }

}

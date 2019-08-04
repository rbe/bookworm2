/*
 * Copyright (C) 2011-2019 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package aoc.datatransfer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.IntStream;

public final class CsvFormat {

    private static final Logger LOGGER = LoggerFactory.getLogger(CsvFormat.class);

    final String EMPTY_STRING = "";

    final String FIELD_SEPARATOR = ",";

    final String FIELD_QUOTE = "\"";

    private final List<CsvField> csvFields;

    private final Map<String, CsvField> csvFieldMap;

    private int index;

    public CsvFormat() {
        csvFields = new ArrayList<>();
        csvFieldMap = new ConcurrentHashMap<>();
        index = -1;
    }

    public void addField(final String name, final String description, final int index) {
        LOGGER.debug("Adding field {}/{} as index {}", name, description, index);
        csvFields.add(new CsvField(name, description, index));
        if (index > this.index) {
            this.index = index;
        }
    }

    public void addField(final String name, final String description) {
        index++;
        LOGGER.debug("Adding field {}/{} as index {}", name, description, index);
        csvFields.add(new CsvField(name, description, index));
    }

    public void addField(final String name, final String description,
                         int numberFrom, int numberTo) {
        IntStream.range(numberFrom, numberTo + 1).forEach(i -> {
            index++;
            LOGGER.debug("Adding field {}{}/{}{} as index {}",
                    name, i, description, i, index);
            csvFields.add(new CsvField(name + i, description + i, index));
        });
    }

    private final Map<Integer, Optional<CsvField>> indexFieldCache = new ConcurrentHashMap<>();
    public Optional<CsvField> fieldAt(final int index) {
        if (indexFieldCache.containsKey(index)) {
            return indexFieldCache.get(index);
        } else {
            final Optional<CsvField> first = csvFields.parallelStream()
                    .filter(f -> f.index == index)
                    .findFirst();
            indexFieldCache.put(index, first);
            return first;
        }
    }

    private final Map<String, Optional<CsvField>> strFieldCache = new ConcurrentHashMap<>();
    public Optional<CsvField> findField(final String str) {
        if (strFieldCache.containsKey(str)) {
            return strFieldCache.get(str);
        } else {
            final Optional<CsvField> first = csvFields.parallelStream()
                    .filter(f -> f.name.equalsIgnoreCase(str) || f.description.equalsIgnoreCase(str))
                    .findFirst();
            strFieldCache.put(str, first);
            return first;
        }
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

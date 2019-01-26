/*
 * Copyright (C) 2011-2019 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package aoc.tools.datatransfer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Scanner;

public final class CsvParser implements As400FileParser {

    private static final Logger LOGGER = LoggerFactory.getLogger(CsvParser.class);

    private static final String EMPTY_STRING = "";

    private static final String FIELD_SEPARATOR = ",";

    private static final String FIELD_QUOTE = "\"";

    private final CsvFormat csvFormat;

    private List<String[]> rows;

    public CsvParser(final CsvFormat csvFormat) {
        Objects.requireNonNull(csvFormat);
        this.csvFormat = csvFormat;
    }

    @Override
    public String[] parseLine(final String line) {
        LOGGER.trace("Parsing line: {}", line);
        final List<String> values = new LinkedList<>();
        try (final Scanner scanner = new Scanner(line)) {
            scanner.useDelimiter(EMPTY_STRING);
            boolean quotedField = false;
            final StringBuilder temp = new StringBuilder();
            while (scanner.hasNext()) {
                String token = scanner.next();
                if (token.equals(FIELD_QUOTE)) {
                    quotedField = !quotedField;
                }
                if (!quotedField && token.equals(FIELD_SEPARATOR)) {
                    values.add(temp.toString());
                    temp.delete(0, temp.length());
                } else if (!token.equals(FIELD_QUOTE)) {
                    temp.append(token);
                }
            }
        }
        final String[] arr = values.toArray(String[]::new);
        if (arr.length > 0) {
            LOGGER.trace("Parsed line into '{}' elements: {}", arr.length, arr);
        } else {
            LOGGER.warn("Cannot parse line: '{}', no elements", line);
        }
        return arr;
    }

    @Override
    public List<String[]> parseLines(final Path path, final Charset charset,
                                     final int expectedLineCount) throws IOException {
        this.rows = As400FileParser.super.parseLines(path, charset, expectedLineCount);
        return rows;
    }

    public List<String[]> getRows() {
        return rows;
    }

    public int size() {
        return rows.size();
    }

    public String[] getRow(int row) {
        return rows.get(row);
    }

    /**
     * Return all values in a column / a field.
     */
    public String[] getColumn(final String field) {
        final Optional<CsvFormat.CsvField> maybeCsvField = csvFormat.findField(field);
        if (maybeCsvField.isPresent()) {
            return rows.parallelStream()
                    .map(s -> s[maybeCsvField.get().getIndex()])
                    .toArray(String[]::new);
        } else {
            throw new CsvFieldNotFoundException(field);
        }
    }

    /**
     * Maybe return value of field.
     * @param field Name or description.
     */
    public String maybeGetValue(final String[] row, final String field) {
        final Optional<String> value = csvFormat.findField(field)
                .map(f -> row[f.getIndex()]);
        if (value.isPresent()) {
            return value.get();
        } else {
            throw new CsvFieldNotFoundException(field);
        }
    }

    public String maybeGetValue(int row, final String field) {
        return maybeGetValue(rows.get(row), field);
    }

    /**
     * Return value of field or an empty string.
     * @param field Name or description.
     */
    public String getValue(final String[] row, final String field) {
        final Optional<CsvFormat.CsvField> maybeCsvField = csvFormat.findField(field);
        if (maybeCsvField.isPresent()) {
            final CsvFormat.CsvField csvField = maybeCsvField.get();
            try {
                return row[csvField.getIndex()];
            } catch (ArrayIndexOutOfBoundsException e) {
                return "";
            }
        } else {
            throw new CsvFieldNotFoundException(field);
        }
    }

    public String getValue(int row, final String field) {
        return getValue(rows.get(row), field);
    }

    public String[] findRowByColumnValue(final String field, final String value) {
        final Optional<String[]> row = csvFormat.findField(field)
                .flatMap(csvField -> rows.parallelStream()
                        .filter(s -> s[csvField.getIndex()].equalsIgnoreCase(value))
                        .findFirst());
        if (row.isPresent()) {
            return row.get();
        } else {
            return new String[0]; //throw new CsvFieldNotFoundException(field);
        }
    }

}

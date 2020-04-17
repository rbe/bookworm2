/*
 * Copyright (C) 2011-2020 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchdienst.domain.required.objectstorage;

import java.io.Serializable;
import java.util.Objects;

/**
 * I represent a AWS S3 bucket name and respect conditions which are
 * <a href="https://docs.aws.amazon.com/AmazonS3/latest/dev/BucketRestrictions.html">documented at AWS: Bucket Restrictions and Limitations</a>.
 */
public final class BucketName implements Serializable {

    private static final long serialVersionUID = -1L;

    private final String name;

    public BucketName(final String name) {
        Objects.requireNonNull(name);
        // Rule: Bucket name cannot be blank
        if (name.isBlank()) {
            throw new IllegalArgumentException("Bucket name should not be blank");
        }
        // Rule: Bucket name should contain lower-case letters and numbers only (DNS-compliant)
        if (name.chars().allMatch(i -> Character.isLowerCase(i) || Character.isDigit(i))) {
            this.name = name;
        } else {
            this.name = null;
        }
        // Rule: Bucket name should not contain space
        if (name.chars().anyMatch(Character::isSpaceChar)) {
            throw new IllegalArgumentException("Bucket name should not contain space");
        }
        // Rule: Bucket name should not contain whitespace
        if (name.chars().anyMatch(Character::isWhitespace)) {
            throw new IllegalArgumentException("Bucket name should not contain whitespace");
        }
        // Rule: Bucket name should not contain supplementary characters
        if (name.chars().anyMatch(Character::isSupplementaryCodePoint)) {
            throw new IllegalArgumentException("Bucket name should not contain supplmentary characters");
        }
        // Rule: Bucket names should not contain upper-case letters
        if (name.chars().anyMatch(Character::isUpperCase)) {
            throw new IllegalArgumentException("Bucket name should not contain upper-case letters");
        }
        // Rule: Bucket names should not contain underscores (_)
        if (name.chars().anyMatch(i -> "LOW LINE".equals(Character.getName(i)))) {
            throw new IllegalArgumentException("Bucket name should not contain underscores");
        }
        // Rule: Bucket names should not end with a dash
        if ("-".equals(name.substring(name.length() - 1))) {
            throw new IllegalArgumentException("Bucket name should not end with a dash");
        }
        // Rule: Bucket names should be between 3 and 63 characters long
        if (3 > name.length() || 63 < name.length()) {
            throw new IllegalArgumentException("Bucket name should be between 3 and 63 characters long");
        }
        // Rule: Bucket names cannot contain periods
        if (name.chars().anyMatch(i -> "FULL STOP".equals(Character.getName(i)))) {
            throw new IllegalArgumentException("Bucket name should not contain a period");
        }
        // Rule: Bucket names cannot contain dashes next to periods (e.g., my-.bucket.com and my.-bucket are invalid)
        // Already done by previous rule
        // Rule: DNS-compatible names
        // TODO Check against COMMA, HYPHEN-MINUS, COLON, SEMICOLON
        if (null == this.name) {
            throw new IllegalArgumentException("Bucket name should contain lower-case letters and numbers only");
        }
    }

    public static BucketName of(String bucketName) {
        return new BucketName(bucketName);
    }

    public String val() {
        return name;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final BucketName that = (BucketName) o;
        return name.equals(that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    @Override
    public String toString() {
        return String.format("BucketName{name='%s'}", name);
    }

}

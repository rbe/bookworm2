/*
 * Copyright (C) 2011-2020 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchdienst.adapter.provided.katalog;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Objects;

public final class SuchindexAnfrageDTO implements Serializable {

    private static final long serialVersionUID = -1L;

    private final String mandant;

    private final String hoerernummer;

    private final String[] keywords;

    public SuchindexAnfrageDTO(final String mandant, final String hoerernummer,
                               final String[] keywords) {
        this.mandant = mandant;
        this.hoerernummer = hoerernummer;
        this.keywords = keywords;
    }

    public String getMandant() {
        return mandant;
    }

    public String getHoerernummer() {
        return hoerernummer;
    }

    public String[] getKeywords() {
        return keywords;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final SuchindexAnfrageDTO that = (SuchindexAnfrageDTO) o;
        return mandant.equals(that.mandant) &&
                hoerernummer.equals(that.hoerernummer) &&
                Arrays.equals(keywords, that.keywords);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(mandant, hoerernummer);
        result = 31 * result + Arrays.hashCode(keywords);
        return result;
    }

    @Override
    public String toString() {
        return String.format("SuchAnfrageDTO{mandant='%s', hoerernummer='%s', keywords=%s}",
                mandant, hoerernummer, Arrays.toString(keywords));
    }

}

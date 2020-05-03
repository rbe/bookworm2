/*
 * Copyright (C) 2011-2020 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchdienst.domain.ports;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

public final class Mandant implements Serializable {

    private static final long serialVersionUID = -1L;

    private final String hoerbuecherei;

    private final List<String> hoerernummern;

    private final List<String> aghNummern;

    private final StorageCredentials storageCredentials;

    public Mandant(final String hoerbuecherei,
                   final List<String> hoerernummern, final List<String> aghNummern,
                   final StorageCredentials storageCredentials) {
        this.hoerbuecherei = hoerbuecherei;
        this.hoerernummern = hoerernummern;
        this.aghNummern = aghNummern;
        this.storageCredentials = storageCredentials;
    }

    public String getHoerbuecherei() {
        return hoerbuecherei;
    }

    public List<String> getHoerernummern() {
        return hoerernummern;
    }

    public List<String> getAghNummern() {
        return aghNummern;
    }

    public StorageCredentials getStorageCredentials() {
        return storageCredentials;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final Mandant mandant = (Mandant) o;
        return hoerbuecherei.equals(mandant.hoerbuecherei) &&
                hoerernummern.equals(mandant.hoerernummern) &&
                aghNummern.equals(mandant.aghNummern);
    }

    @Override
    public int hashCode() {
        return Objects.hash(hoerbuecherei, hoerernummern, aghNummern);
    }

    @Override
    public String toString() {
        return String.format("Mandant{hoerbuecherei='%s', hoerernummern=%d Einträge, aghNummern=%d Einträge}",
                hoerbuecherei, hoerernummern.size(), aghNummern.size());
    }

}

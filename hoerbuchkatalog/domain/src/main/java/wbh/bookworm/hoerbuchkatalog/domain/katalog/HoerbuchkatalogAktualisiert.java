/*
 * Copyright (C) 2011-2018 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchkatalog.domain.katalog;

import java.util.Objects;

public final class HoerbuchkatalogAktualisiert extends HoerbuchkatalogEvent {

    private static final long serialVersionUID = -1L;

    private final HoerbuchkatalogId hoerbuchkatalogDomainId;

    private final long neueVersion;

    public HoerbuchkatalogAktualisiert(final HoerbuchkatalogId hoerbuchkatalogDomainId,
                                       final long neueVersion) {
        this.hoerbuchkatalogDomainId = hoerbuchkatalogDomainId;
        this.neueVersion = neueVersion;
    }

    public HoerbuchkatalogId getHoerbuchkatalogDomainId() {
        return hoerbuchkatalogDomainId;
    }

    public long getNeueVersion() {
        return neueVersion;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        final HoerbuchkatalogAktualisiert that = (HoerbuchkatalogAktualisiert) o;
        return neueVersion == that.neueVersion &&
                Objects.equals(hoerbuchkatalogDomainId, that.hoerbuchkatalogDomainId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), hoerbuchkatalogDomainId, neueVersion);
    }

}

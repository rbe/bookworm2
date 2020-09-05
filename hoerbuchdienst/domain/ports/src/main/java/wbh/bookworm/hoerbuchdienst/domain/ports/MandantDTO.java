/*
 * Copyright (C) 2011-2020 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchdienst.domain.ports;

import java.io.Serializable;
import java.util.Collections;
import java.util.Objects;
import java.util.Set;

public final class MandantDTO implements Serializable {

    private static final long serialVersionUID = -1L;

    private final String id;

    private final Set<String> hoerernummern;

    private final Set<String> aghNummern;

    public MandantDTO(final String id, final Set<String> hoerernummern, final Set<String> aghNummern) {
        Objects.requireNonNull(id);
        this.id = id;
        if (null != hoerernummern) {
            this.hoerernummern = Set.copyOf(hoerernummern);
        } else {
            this.hoerernummern = Collections.emptySet();
        }
        if (null != aghNummern) {
            this.aghNummern = Set.copyOf(aghNummern);
        } else {
            this.aghNummern = Collections.emptySet();
        }
    }

    public String getId() {
        return id;
    }

    public Set<String> getHoerernummern() {
        return hoerernummern;
    }

    public Set<String> getAghNummern() {
        return aghNummern;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final MandantDTO that = (MandantDTO) o;
        return id.equals(that.id) &&
                hoerernummern.equals(that.hoerernummern) &&
                aghNummern.equals(that.aghNummern);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, hoerernummern, aghNummern);
    }

    @Override
    public String toString() {
        return String.format("MandantDTO{id='%s', hoerernummern=%d, aghNummern=%d}",
                id, hoerernummern.size(), aghNummern.size());
    }

}

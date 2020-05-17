/*
 * Copyright (C) 2011-2020 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchdienst.domain.ports.mandant;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

public final class MandantDTO implements Serializable {

    private static final long serialVersionUID = -1L;

    private final String hoerbuecherei;

    private final List<String> hoerernummern;

    private final List<String> aghNummern;

    private final String storageUrl;

    private final String accessKey;

    private final String secretKey;

    public MandantDTO(final String hoerbuecherei,
                      final List<String> hoerernummern, final List<String> aghNummern,
                      final String storageUrl, final String accessKey, final String secretKey) {
        this.hoerbuecherei = hoerbuecherei;
        this.hoerernummern = hoerernummern;
        this.aghNummern = aghNummern;
        this.storageUrl = storageUrl;
        this.accessKey = accessKey;
        this.secretKey = secretKey;
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

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final MandantDTO that = (MandantDTO) o;
        return hoerbuecherei.equals(that.hoerbuecherei) &&
                hoerernummern.equals(that.hoerernummern) &&
                aghNummern.equals(that.aghNummern) &&
                storageUrl.equals(that.storageUrl) &&
                accessKey.equals(that.accessKey) &&
                secretKey.equals(that.secretKey);
    }

    @Override
    public int hashCode() {
        return Objects.hash(hoerbuecherei, hoerernummern, aghNummern, storageUrl, accessKey, secretKey);
    }

    @Override
    public String toString() {
        return String.format("MandantDTO{hoerbuecherei='%s', hoerernummern=%s, aghNummern=%s, storageUrl='%s', accessKey='%d' bytes, secretKey='%d' bytes}",
                hoerbuecherei, hoerernummern, aghNummern, storageUrl, accessKey.length(), secretKey.length());
    }

}

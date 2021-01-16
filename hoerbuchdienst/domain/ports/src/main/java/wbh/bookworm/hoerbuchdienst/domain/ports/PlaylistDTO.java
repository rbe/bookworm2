/*
 * Copyright (C) 2011-2020 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchdienst.domain.ports;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

public final class PlaylistDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = -8820698782758681073L;

    private final String titelnummer;

    private final List<PlaylistEntryDTO> entries;

    public PlaylistDTO(final String titelnummer, final List<PlaylistEntryDTO> entries) {
        this.titelnummer = titelnummer;
        this.entries = entries;
    }

    public String getTitelnummer() {
        return titelnummer;
    }

    public List<PlaylistEntryDTO> getEntries() {
        return entries;
    }

    public void add(PlaylistEntryDTO entry) {
        entries.add(entry);
    }

    public void addAll(final List<PlaylistEntryDTO> entries) {
        this.entries.addAll(entries);
    }

    @Override
    public String toString() {
        return String.format("PlaylistDTO{titelnummer='%s', entries=%s}", titelnummer, entries);
    }

}

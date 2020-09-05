/*
 * Copyright (C) 2011-2020 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchdienst.domain.ports;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public final class PlaylistDTO implements Serializable {

    private static final long serialVersionUID = -8820698782758681073L;

    private List<PlaylistEntryDTO> entries;

    public PlaylistDTO() {
        entries = new ArrayList<>();
    }

    public PlaylistDTO(final List<PlaylistEntryDTO> entries) {
        this.entries = entries;
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

}

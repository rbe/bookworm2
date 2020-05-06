/*
 * Copyright (C) 2011-2020 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchdienst.adapter.provided.stream;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import wbh.bookworm.hoerbuchdienst.domain.ports.audiobook.PlaylistEntryDTO;

public final class PlaylistAntwortDTO implements Serializable {

    private static final long serialVersionUID = -1729563029415037788L;

    private List<PlaylistEntryDTO> entries;

    public PlaylistAntwortDTO() {
        entries = new ArrayList<>();
    }

    public PlaylistAntwortDTO(final List<PlaylistEntryDTO> entries) {
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

/*
 * Copyright (C) 2011-2020 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchdienst.domain.ports;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public final class PlaylistDTO {

    private List<PlaylistEntry> entries;

    public PlaylistDTO() {
        entries = new ArrayList<>();
    }

    public void add(PlaylistEntry entry) {
        entries.add(entry);
    }

    public void addAll(final List<PlaylistEntry> entries) {
        this.entries.addAll(entries);
    }

}

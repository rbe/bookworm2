/*
 * Copyright (C) 2011-2020 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchdienst.domain.ports;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PlaylistEntry {

    private static final Double[] NO_CLIPS = {};

    private String title;

    private String ident;

    private Double[] clips;

    public PlaylistEntry(final String title, final String ident) {
        this.title = title;
        this.ident = ident;
        this.clips = NO_CLIPS;
    }

}

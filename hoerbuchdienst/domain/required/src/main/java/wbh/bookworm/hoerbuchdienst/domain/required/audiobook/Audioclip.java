/*
 * Copyright (C) 2011-2020 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchdienst.domain.required.audiobook;

import java.io.Serializable;
import java.time.Duration;

import lombok.Data;

@Data
public final class Audioclip implements Serializable {

    private static final long serialVersionUID = -1L;

    private final String filename;

    private final Duration begin;

    private final Duration endExclusive;

}

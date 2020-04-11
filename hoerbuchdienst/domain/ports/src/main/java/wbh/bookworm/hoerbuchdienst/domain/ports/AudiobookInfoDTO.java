/*
 * Copyright (C) 2011-2020 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchdienst.domain.ports;

import java.time.Duration;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Setter;

@Data
@Setter
@AllArgsConstructor
public class AudiobookInfoDTO {

    private /*TODO Titelnummer*/ String titelnummer;

    private final String titel;

    private final String autor;

    private final String sprecher;

    private final Duration spieldauer;

}

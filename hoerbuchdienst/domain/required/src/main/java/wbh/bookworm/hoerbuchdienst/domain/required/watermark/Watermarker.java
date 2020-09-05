/*
 * Copyright (C) 2011-2020 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchdienst.domain.required.watermark;

import java.nio.file.Path;

public interface Watermarker {

    String makeWatermark(String mandant, String hoerernummer, String titelnummer);

    WatermarkedTrackInfo trackInfo(String watermark, String urlPrefix, Path mp3);

    void addWatermarkInPlace(final String watermark, String urlPrefix, Path mp3);

}

/*
 * Copyright (C) 2011-2020 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package wbh.bookworm.hoerbuchdienst.domain.required.watermark;

import java.io.File;

public interface Watermarker {

    TrackInfoDTO trackInfo(String watermark, String urlPrefix, File file);

    File addWatermark(final String watermark, String urlPrefix, File file);

}

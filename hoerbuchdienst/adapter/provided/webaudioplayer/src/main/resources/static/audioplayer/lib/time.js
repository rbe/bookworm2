/*
 * Copyright (C) 2011-2020 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

"use strict";

export class Time {

    static format(time) {
        const hr = ~~(time / 3600);
        const min = ~~((time % 3600) / 60);
        const sec = time % 60;
        let sec_min = "";
        if (hr > 0) {
            sec_min += "" + hrs + ":" + (min < 10 ? "0" : "");
        }
        sec_min += "" + min + ":" + (sec < 10 ? "0" : "");
        sec_min += "" + sec;
        return sec_min;
    }

}

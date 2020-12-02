/*
 * Copyright (C) 2020 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

"use strict";

import {Wbhonline} from "./js/wbhonline.js";

document.addEventListener('DOMContentLoaded', function () {
    const wbhonline = new Wbhonline('80170');
    wbhonline.hoerprobeButtons();
    wbhonline.merklisteButtons();
    wbhonline.warenkorbButtons();
    wbhonline.downloadButtons();
});

/*
 * Copyright (C) 2020 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

"use strict";

import {WbhonlineMerkliste} from "./wbhonlineMerkliste.js";
import {WbhonlineWarenkorb} from "./wbhonlineWarenkorb.js";
import {WbhonlineCdBestellung} from "./wbhonlineCdBestellung.js";
import {WbhonlineDownloads} from "./wbhonlineDownloads.js";
import {WbhonlineHoerprobe} from "./wbhonlineHoerprobe.js";
import {WbhonlineHoererarchiv} from "./wbhonlineHoererarchiv.js";

export class Wbhonline {

    constructor() {
        const merkliste = new WbhonlineMerkliste();
        const warenkorb = new WbhonlineWarenkorb();
        const cdBestellung = new WbhonlineCdBestellung();
        const download = new WbhonlineDownloads();
        const hoerprobe = new WbhonlineHoerprobe();
        const hoererarchiv = new WbhonlineHoererarchiv();
    }

}

document.addEventListener('DOMContentLoaded', (event) => {
    const wbhonline = new Wbhonline();
});

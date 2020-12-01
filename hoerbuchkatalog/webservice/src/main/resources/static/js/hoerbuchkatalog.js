/*
 * Copyright (C) 2020 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

"use strict";

import {Bookworm} from "./lib/bookworm.js";

window.onload = function () {
    const bookworm = new Bookworm();
    // Hörprobe
    const hoerprobeButton = document.querySelector('.button .hoerprobe');
    hoerprobeButton.addEventListener('click', () => {
        const titelnummer = document.querySelector('#titelnummer').value;
        bookworm.hoerprobe(titelnummer);
    });
    // Merkliste
    const merklisteButton = document.querySelector('.button .watchlist-false');
    merklisteButton.addEventListener('click', function (e) {
        console.log(e);
        console.log(this);
    });
    // CD Bestellung
    const cdBestellenButton = document.querySelector('.button .order-cd-false');
    cdBestellenButton.addEventListener('click', function (e) {
        console.log(e);
        console.log(this);
    });
    // Download
    const downloadButton = document.querySelector('.button .order-download-true');
    downloadButton.addEventListener('click', () => {
    });
};

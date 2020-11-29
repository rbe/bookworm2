/*
 * Copyright (C) 2020 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

"use strict";

import {Audioplayer} from "./lib/audioplayer";

const bookworm = {

    fuegeZuMerklisteHinzu: function (titelnummer) {
    },

    fuegeZuWarenkorbHinzu: function (titelnummer) {
    },

    bestelleDownload: function (titelnummer) {
    }

};

window.onload = function () {
    const audioplayer = new Audioplayer();
    const hoerbuchdienstUrl = new URL('https://hoerbuchdienst.shard4.audiobook.wbh-online.de');
    audioplayer.init(hoerbuchdienstUrl, '06', '00000');
    document.querySelector('.button .order-cd').addEventListener('click', function (e) {
        console.log(e);
        console.log(this);
    });
    document.querySelector('.button .order-download-true').addEventListener('click', () => {
    });
    document.querySelector('.button .watchlist').addEventListener('click', () => {
    });
};

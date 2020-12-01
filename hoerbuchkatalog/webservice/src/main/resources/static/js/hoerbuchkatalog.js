/*
 * Copyright (C) 2020 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

"use strict";

import {Audioplayer} from "./lib/audioplayer.js";

const bookworm = {

    audioplayer: new Audioplayer('https://hoerbuchdienst.shard4.audiobook.wbh-online.de', '06', '00000'),

    fuegeZuMerklisteHinzu: function (titelnummer) {
    },

    entferneVonMerkliste: function (titelnummer) {
    },

    fuegeZuWarenkorbHinzu: function (titelnummer) {
    },

    entferneVonWarenkorb: function (titelnummer) {
    },

    hoerprobe: function (titelnummer) {
        this.audioplayer.hoerprobe(titelnummer);
    },

    bestelleDownload: function (titelnummer) {
    }

};

window.onload = function () {
    document.querySelector('#hoerprobeAbrufen').addEventListener('click', () => {
        const titelnummer = document.querySelector('#titelnummer').value;
        bookworm.audioplayer.hoerprobe(titelnummer);
    });
    /*
    document.querySelector('.button .order-cd').addEventListener('click', function (e) {
        console.log(e);
        console.log(this);
    });
    document.querySelector('.button .order-download-true').addEventListener('click', () => {
    });
    document.querySelector('.button .watchlist').addEventListener('click', () => {
    });
    */
};

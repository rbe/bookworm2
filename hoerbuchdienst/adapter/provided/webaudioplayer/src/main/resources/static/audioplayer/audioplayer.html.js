/*
 * Copyright (C) 2019-2020 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

"use strict";

import {DomEventBus} from "./lib/domEventBus.js";
import {Audioplayer} from "./lib/audioplayer.js";

(function () {
    const events = [
        'audiobookSelected', 'audiobookLoaded',
        'play', 'pause',
        'previous10Seconds', 'next10Seconds',
        'previousPhrase', 'nextPhrase',
        'previousTrack', 'nextTrack',
        'volumeUp', 'volumeDown',
        'trackSelected',
        'phraseSelected'
    ];
    DomEventBus.setup(events);
    document.querySelector('.playlistContainer .loading').style.display = 'none';
    document.querySelector('.playlistContainer .notfound').style.display = 'none';
    document.querySelector('.audioplayer .loading').style.display = 'none';
    document.querySelector('.audioplayer .notfound').style.display = 'none';
    document.querySelector('.audioplayer .panel').style.display = 'none';
    // sync download
    const syncDownloadButton = document.querySelector('#syncDownloadButton');
    syncDownloadButton.style.display = 'none';
    const syncDownloadStatusText = document.querySelector('#asyncDownloadStatus');
    syncDownloadStatusText.style.display = 'block';
    // async download
    const asyncDownloadButton = document.querySelector('#asyncDownloadButton');
    asyncDownloadButton.style.display = 'none';
    const asyncDownloadStatusText = document.querySelector('#asyncDownloadStatus');
    asyncDownloadStatusText.style.display = 'block';
    const audioplayer = new Audioplayer();
    document.querySelector('#ladenButton').addEventListener('click', event => {
        document.querySelector('.playlistContainer .notfound').style.display = 'none';
        document.querySelector('.audioplayer .panel').style.display = 'none';
        document.querySelector('.audioplayer .notfound').style.display = 'none';
        document.querySelector('.playlistContainer .loading').style.display = 'block';
        document.querySelector('.audioplayer .loading').style.display = 'block';
        audioplayer.reset();
        const b = window.location;
        const url = new URL(b.protocol + '//' + b.host);
        const mandant = document.querySelector('#mandant').value;
        const hoerernummer = document.querySelector('#hoerernummer').value;
        const titelnummer = document.querySelector('#titelnummer').value;
        audioplayer.init(url, mandant, hoerernummer, titelnummer, () => {
            document.querySelector('.playlistContainer .loading').style.display = 'none';
            document.querySelector('.playlistContainer .notfound').style.display = 'none';
            document.querySelector('.audioplayer .loading').style.display = 'none';
            document.querySelector('.audioplayer .notfound').style.display = 'none';
            document.querySelector('.audioplayer .panel').style.display = 'block';
            asyncDownloadButton.style.display = 'block';
        });
    });
    // sync download
    syncDownloadButton.addEventListener('click', event => {
        if (audioplayer) {
            audioplayer.syncDownload();
        } else {
            console.log('Cannot download, Audioplayer not initialized');
        }
    });
    // async download
    asyncDownloadButton.addEventListener('click', event => {
        if (audioplayer) {
            audioplayer.asyncDownloadOrder();
        } else {
            console.log('Cannot download, Audioplayer not initialized');
        }
    });
})();

/*
 * Copyright (C) 2019-2020 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

"use strict";

import {DomEventBus} from "./lib/domEventBus.js";
import {Audioplayer} from "./lib/audioplayer.js";

function hideElements() {
    // Playlist
    document.querySelector('.playlistContainer').style.display = 'none';
    document.querySelector('.playlistContainer .loading').style.display = 'none';
    document.querySelector('.playlistContainer .notfound').style.display = 'none';
    // Audioplayer
    document.querySelector('.audioplayer').style.display = 'none';
    document.querySelector('.audioplayer .loading').style.display = 'none';
    document.querySelector('.audioplayer .notfound').style.display = 'none';
    document.querySelector('.audioplayer .panel').style.display = 'none';
    // sync download
    document.querySelector('#syncDownloadButton').style.display = 'none';
    document.querySelector('#syncDownloadStatus').style.display = 'none';
    // async download
    document.querySelector('#asyncDownloadButton').style.display = 'none';
    document.querySelector('#asyncDownloadStatus').style.display = 'none';
}

function showElementsLoading() {
    // Playlist
    document.querySelector('.playlistContainer .notfound').style.display = 'none';
    document.querySelector('.playlistContainer .loading').style.display = 'block';
    document.querySelector('.playlistContainer').style.display = 'block';
    // Audioplayer
    document.querySelector('.audioplayer .panel').style.display = 'none';
    document.querySelector('.audioplayer .notfound').style.display = 'none';
    document.querySelector('.audioplayer .loading').style.display = 'block';
    document.querySelector('.audioplayer').style.display = 'block';
    // sync download
    document.querySelector('#syncDownloadButton').style.display = 'none';
    document.querySelector('#syncDownloadStatus').style.display = 'none';
    // async download
    document.querySelector('#asyncDownloadButton').style.display = 'none';
    document.querySelector('#asyncDownloadStatus').style.display = 'none';
}

function showElementsLoaded() {
    // Playlist
    document.querySelector('.playlistContainer .notfound').style.display = 'none';
    document.querySelector('.playlistContainer .loading').style.display = 'none';
    // Audioplayer
    document.querySelector('.audioplayer .loading').style.display = 'none';
    document.querySelector('.audioplayer .notfound').style.display = 'none';
    document.querySelector('.audioplayer .panel').style.display = 'block';
    // sync download
    document.querySelector('#syncDownloadButton').style.display = 'block';
    document.querySelector('#syncDownloadStatus').style.display = 'block';
    // async download
    document.querySelector('#asyncDownloadButton').style.display = 'block';
    document.querySelector('#asyncDownloadStatus').style.display = 'block';
}

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
    hideElements();
    const audioplayer = new Audioplayer();
    // sync download
    document.querySelector('#syncDownloadButton').addEventListener('click', event => {
        if (audioplayer) {
            audioplayer.syncDownload();
        } else {
            console.log('Cannot download, Audioplayer not initialized');
        }
    });
    // async download
    document.querySelector('#asyncDownloadButton').addEventListener('click', event => {
        if (audioplayer) {
            audioplayer.asyncDownloadOrder();
        } else {
            console.log('Cannot download, Audioplayer not initialized');
        }
    });
    document.querySelector('#ladenButton').addEventListener('click', event => {
        showElementsLoading();
        audioplayer.reset();
        const b = window.location;
        const url = new URL('https://hoerbuchdienst-shard11.wbh-online.de');
        const mandant = document.querySelector('#mandant').value;
        const hoerernummer = document.querySelector('#hoerernummer').value;
        const titelnummer = document.querySelector('#titelnummer').value;
        audioplayer.init(url, mandant, hoerernummer, titelnummer, () => {
            showElementsLoaded();
        });
    });
})();

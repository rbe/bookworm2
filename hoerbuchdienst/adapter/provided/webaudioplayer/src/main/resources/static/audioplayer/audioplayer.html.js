/*
 * Copyright (C) 2019-2020 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

"use strict";

import {DomEventBus} from "./lib/domEventBus.js";
import {Audioplayer} from "./audioplayer.js";

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
    const audioplayer = new Audioplayer();
    document.querySelector('.playlistContainer .loading').style.display = 'none';
    document.querySelector('.playlistContainer .notfound').style.display = 'none';
    document.querySelector('.audioplayer .loading').style.display = 'none';
    document.querySelector('.audioplayer .notfound').style.display = 'none';
    document.querySelector('.audioplayer .panel').style.display = 'none';
    document.querySelector('#anhoerenButton')
        .addEventListener('click', event => {
            audioplayer.reset();
            const hoerernummer = document.querySelector('#hoerernummer').value;
            const titelnummer = document.querySelector('#titelnummer').value;
            const b = window.location;
            const url = new URL(b.protocol + '//' + b.host + '/stream/' + hoerernummer + '/' + titelnummer + '/');
            document.querySelector('.playlistContainer .loading').style.display = 'block';
            document.querySelector('.audioplayer .panel').style.display = 'none';
            document.querySelector('.audioplayer .loading').style.display = 'block';
            audioplayer.init(url, () => {
                document.querySelector('.playlistContainer .loading').style.display = 'none';
                document.querySelector('.playlistContainer .notfound').style.display = 'none';
                document.querySelector('.audioplayer .loading').style.display = 'none';
                document.querySelector('.audioplayer .notfound').style.display = 'none';
                document.querySelector('.audioplayer .panel').style.display = 'block';
            });
        });
})();

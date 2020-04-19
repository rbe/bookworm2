/*
 * Copyright (C) 2019-2020 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

"use strict";

class Localstorage {

    constructor() {
        this.prefix = 'audioplayer';
    }

    updateCurrentTrack(trackNumber, currentTime) {
        window.localStorage.setItem(this.prefix + '.track.number', JSON.stringify(trackNumber));
        window.localStorage.setItem(this.prefix + '.track.currentTime', JSON.stringify(currentTime));
    }

    currentTrack() {
        return [
            window.localStorage.getItem(this.prefix + '.track.number'),
            window.localStorage.getItem(this.prefix + '.track.currentTime')
        ];
    }

}

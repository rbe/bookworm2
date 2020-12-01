/*
 * Copyright (C) 2019-2020 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

"use strict";

import {FetchErrorHandler} from "./fetchErrorHandler.js";

export class Audioplayer {

    constructor(audiobookURL, mandant, hoerernummer) {
        this.audiobookURL = audiobookURL;
        this.mandant = mandant;
        this.hoerernummer = hoerernummer;
        this.audio = document.querySelector('#audio');
        this.asyncDownloadStatusTimeoutId = new Map();
    }

    createAudioElement() {
        const audio = document.createElement('audio');
        audio.id = 'audio';
        audio.load();
        return audio;
    }

    hoerprobe(titelnummer) {
        const url = new URL('v1/hoerprobe/' + titelnummer, this.audiobookURL);
        fetch(url.toString(), {
            'method': 'GET',
            'headers': {
                'Accept': 'audio/mp3',
                'X-Bookworm-Mandant': this.mandant,
                'X-Bookworm-Hoerernummer': this.hoerernummer
            },
            'redirect': 'follow'
        })
            .then(response => {
                if (response.ok) {
                    return response.blob();
                } else {
                    FetchErrorHandler.handle(response);
                }
            })
            .then(blob => {
                this.audio.src = URL.createObjectURL(blob);
                this.audio.load();
                this.audio.play();
            })
            .catch(reason => {
                console.log('Fehler: ' + reason);
            });
    }

}

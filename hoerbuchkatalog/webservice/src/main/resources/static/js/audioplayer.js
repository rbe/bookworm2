/*
 * Copyright (C) 2019-2020 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

"use strict";

import {FetchErrorHandler} from "./fetchErrorHandler.js";

export class Audioplayer {

    constructor(shardURL, mandant, hoerernummer) {
        this.shardURL = shardURL;
        this.mandant = mandant;
        this.hoerernummer = hoerernummer;
    }

    createAudioElement(titelnummer) {
        const audio = document.createElement('audio');
        audio.id = 'audio-' + titelnummer;
        audio.load();
        return audio;
    }

    hoerprobe(titelnummer, audio, playCallback, pauseCallback) {
        const url = new URL('v1/hoerprobe/' + titelnummer, this.shardURL);
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
                if (audio === undefined || audio === null) {
                    audio = this.createAudioElement(titelnummer);
                }
                audio.src = URL.createObjectURL(blob);
                audio.load();
                audio.play();
                audio.addEventListener('play', () => {
                    if (playCallback) {
                        playCallback();
                    }
                });
                audio.addEventListener('pause', () => {
                    if (pauseCallback) {
                        pauseCallback();
                    }
                });
                audio.addEventListener('ended', () => {
                    URL.revokeObjectURL(audio.src);
                    if (pauseCallback) {
                        pauseCallback();
                    }
                });
            })
            .catch(reason => {
                console.log('Fehler: ' + reason);
            });
    }

}

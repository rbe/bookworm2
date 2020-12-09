/*
 * Copyright (C) 2019-2020 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

"use strict";

import {BookwormRestClient} from "./bookwormRestClient.js";

export class Audioplayer {

    constructor(mandant, hoerernummer) {
        this.bookwormRestClient = new BookwormRestClient(mandant, hoerernummer);
    }

    createAudioElement(titelnummer) {
        const audio = document.createElement('audio');
        audio.id = 'audio-' + titelnummer;
        audio.load();
        return audio;
    }

    cleanup(audio) {
        URL.revokeObjectURL(audio.src);
        audio.remove();
    }

    spieleHoerprobeAb(titelnummer, audio, element, playCallback, pauseCallback) {
        this.bookwormRestClient.bestelleHoerprobe(titelnummer, (blob) => {
            if (audio === undefined || audio === null) {
                audio = this.createAudioElement(titelnummer);
            }
            this.audio = audio;
            audio.src = URL.createObjectURL(blob);
            audio.load();
            audio.addEventListener('play', () => {
                if (playCallback) {
                    playCallback(element);
                }
            });
            audio.addEventListener('pause', () => {
                this.pausiereHoerprobe(element, pauseCallback);
            });
            audio.addEventListener('ended', () => {
                this.cleanup(audio);
            });
            audio.play();
        });
    }

    pausiereHoerprobe(element, callback) {
        if (this.audio) {
            this.audio.pause();
            this.cleanup(audio);
            if (callback) {
                callback(element);
            }
        }
    }

}

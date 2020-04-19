/*
 * Copyright (C) 2019-2020 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

"use strict";

export class VolumeControl {

    constructor(elementSelectors, audio) {
        this.audio = audio;
        this.volumeDownButton = document.querySelector(elementSelectors.volumeDownButtonSelector);
        this.volumeUpButton = document.querySelector(elementSelectors.volumeUpButtonSelector);
        this.volumeSlider = document.querySelector(elementSelectors.volumeSliderInputSelector);
        this.initVolumeButtons();
        this.initVolumeEvents();
        this.syncSlider();
    }

    initVolumeEvents() {
        this.volumeSlider.addEventListener('input', event => {
            const slider = event.target;
            this.audio.volume = parseInt(slider.value) / 10;
        })
    }

    initVolumeButtons() {
        this.volumeDownButton.addEventListener('click', () => this.down());
        this.volumeUpButton.addEventListener('click', () => this.up());
        // TODO volumeSlider
    }

    up() {
        if (this.audio.volume < 1.0) {
            this.audio.volume += 0.1;
            this.syncSlider();
        } else {
            //console.log('Volume is at max (' + this.audio.volume + ')')
        }
    }

    down() {
        if (this.audio.volume > 0.1) {
            this.audio.volume -= 0.1;
            this.syncSlider();
        } else {
            //console.log('Volume is (' + this.audio.volume + ')');
            this.playerControls.pause();
        }
    }

    syncSlider() {
        this.volumeSlider.value = this.audio.volume * 10;
    }

}

/*
 * Copyright (C) 2011-2020 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

"use strict";

export class PlayerControl {

    constructor(audio) {
        this.audio = audio;
        this.playlistSelector = 'div.playlist';
        this.volumeDownButtonSelector = 'button.volumeDown';
        this.volumeUpButtonSelector = 'button.volumeUp';
        this.volumeSliderInputSelector = 'input.volumeSlider';
        this.volumeDownButton = document.querySelector(this.volumeDownButtonSelector);
        this.volumeUpButton = document.querySelector(this.volumeUpButtonSelector);
        this.volumeSlider = document.querySelector(this.volumeSliderInputSelector);
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

/*
 * Copyright (C) 2019-2020 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

"use strict";

import {Time} from "./lib/time.js";
import {FetchErrorHandler} from "./lib/fetchErrorHandler.js";
import {Playlist} from "./playlist.js";
import {VolumeControl} from "./volumeControl.js";

export class Audioplayer {

    constructor() {
        this.audio = this.createAudioElement();
        this.initElementSelectors();
        this.DEBUG = false;
    }

    init(audiobookURL, onReadyCallback) {
        this.audiobookURL = audiobookURL;
        this.playButton = document.querySelector(this.elementSelectors.playButtonSelector);
        this.currentTrackTitle = document.querySelector(this.elementSelectors.currentTrackTitleSelector);
        this.displayAudiobookInfo();
        this.playlist = new Playlist(this.elementSelectors, this, this.audiobookURL, () => {
            this.currentTrack = -1;
            this.initAudioEvents();
            this.initPlayerControls();
            this.volumeControl = new VolumeControl(this.elementSelectors, this.audio);
            onReadyCallback();
        });
    }

    createAudioElement() {
        const audio = document.createElement('audio');
        audio.id = 'audio';
        audio.load();
        return audio;
    }

    initElementSelectors() {
        this.elementSelectors = {
            'playlistSelector': 'div.playlist',
            'playlistEntriesSelector': 'div.playlist ul',
            'titleTextSelector': 'div.titel > span',
            'titelnummerTextSelector': 'div.titelnummer > span',
            'authorTextSelector': 'div.author > span',
            'narratorTextSelector': 'div.narrator > span',
            'currentTrackTitleSelector': 'div.title > span',
            'trackInfoTextSelector': 'div.trackInfo > pre',
            'currentTimeSelector': '#currentTime',
            'maxTimeSelector': '#maxTime',
            'progressBarSelector': 'div.progressbar',
            'previousTrackButtonSelector': 'button.previousTrack',
            'prev10TrackButtonSelector': 'button.prev10',
            'playButtonSelector': 'button.play',
            'next10TrackButtonSelector': 'button.next10',
            'nextTrackButtonSelector': 'button.nextTrack',
            'volumeDownButtonSelector': 'button.volumeDown',
            'volumeUpButtonSelector': 'button.volumeUp',
            'volumeSliderInputSelector': 'input.volumeSlider'
        };
    }

    displayAudiobookInfo() {
        fetch(new URL('info', this.audiobookURL).toString())
            .then(response => {
                if (response.ok) {
                    return response.json();
                } else {
                    FetchErrorHandler.handle(response);
                }
            })
            .then(json => {
                if (json) {
                    document.querySelector(this.elementSelectors.titleTextSelector).innerHTML = json.titel;
                    document.querySelector(this.elementSelectors.titelnummerTextSelector).innerHTML = json.titelnummer;
                    document.querySelector(this.elementSelectors.authorTextSelector).innerHTML = json.autor;
                    document.querySelector(this.elementSelectors.narratorTextSelector).innerHTML = json.sprecher;
                }
            })
            .catch(reason => {
                this.console('displayAudiobookInfo,fetch,catch: ' + reason);
            });
    }

    initAudioEvents() {
        const currentTimeElement = document.querySelector(this.elementSelectors.currentTimeSelector);
        const maxTimeElement = document.querySelector(this.elementSelectors.maxTimeSelector);
        const progressbar = document.querySelector(this.elementSelectors.progressBarSelector);
        this.audio.addEventListener('timeupdate', event => {
            const currentTime = parseInt(this.audio.currentTime, 10);
            currentTimeElement.innerHTML = Time.format(currentTime);
            const pct = (this.audio.currentTime / this.audio.duration) * 100;
            progressbar.style.width = parseInt(pct, 10) + "%";
        });
        this.audio.addEventListener('canplaythrough', event => {
            maxTimeElement.innerHTML = Time.format(parseInt(this.audio.duration, 10));
        });
        this.audio.addEventListener('ended', event => {
            const playButtonSaysPlaying = this.playButton.innerText === 'Pause';
            const moreTracksAvailable = this.currentTrack < this.playlist.highestTrackIndex;
            if (playButtonSaysPlaying && moreTracksAvailable) {
                this.playNext();
            } else {
                this.pause();
            }
        });
    }

    playPauseFunction = event => {
        const elt = event.target;
        switch (elt.innerText) {
            case 'Play':
                if (this.currentTrack === -1) {
                    this.selectTrack(0, 0);
                }
                this.play();
                break;
            case 'Pause':
                this.pause();
                break;
        }
    };
    initPlayerControls() {
        this.playButton.addEventListener('click', this.playPauseFunction);
        this.previousTrackButton = document.querySelector(this.elementSelectors.previousTrackButtonSelector);
        this.previousTrackButton.addEventListener('click', () => this.playPrevious());
        this.prev10TrackButton = document.querySelector(this.elementSelectors.prev10TrackButtonSelector);
        this.prev10TrackButton.addEventListener('click', () => this.prev10());
        this.next10TrackButton = document.querySelector(this.elementSelectors.next10TrackButtonSelector);
        this.next10TrackButton.addEventListener('click', () => this.next10());
        this.nextTrackButton = document.querySelector(this.elementSelectors.nextTrackButtonSelector);
        this.nextTrackButton.addEventListener('click', () => this.playNext());
    }

    selectTrack(trackIndex, currentSecs = 0.0) {
        if (this.currentTrack > -1) {
            const id = this.playlist.trackInfo(this.currentTrack).playlistElementId;
            const elements = document.querySelectorAll('#' + id);
            for (const element of elements) {
                element.classList.remove('currently-playing');
            }
        }
        document.querySelector(this.elementSelectors.trackInfoTextSelector).innerHTML = '&nbsp;';
        trackIndex = parseInt(trackIndex);
        const track = this.playlist.trackInfo(trackIndex);
        this.currentTrackTitle.innerText = track.title || track.ident;
        fetch(new URL('track/' + track.ident + '/info', this.audiobookURL).toString())
            .then(response => {
                if (response.ok) {
                    return response.json();
                } else {
                    FetchErrorHandler.handle(response);
                }
            })
            .then(json => {
                if (json) {
                    document.querySelector(this.elementSelectors.trackInfoTextSelector)
                        .innerHTML = JSON.stringify(json, undefined, 2);
                }
            })
            .catch(reason => {
                this.console('play,fetch,catch: ' + reason);
            });
        const trackId = track.playlistElementId;
        document.querySelector('#' + trackId).classList.add('currently-playing');
        const url = new URL('track/' + track.ident, this.audiobookURL);
        this.console('selectTrack: audio.src=' + url.toString());
        this.audio.src = url.toString();
        this.console('selectTrack: audio.load()');
        this.audio.load();
        if (currentSecs > 0) {
            this.audio.currentTime = parseFloat(currentSecs);
        }
        this.currentTrack = trackIndex;
    }

    play() {
        const playPromise = this.audio.play();
        if (undefined !== playPromise) {
            playPromise.then(param => {
                this.playButton.innerText = 'Pause';
                this.console('play(): promise ended, param=' + param);
            }).catch(error => {
                this.console('play(): audio.play() was prevented: ' + error);
            });
        } else {
            this.console('play(): No promise');
        }
    }

    pause() {
        this.audio.pause();
        this.playButton.innerText = 'Play';
    }

    playPrevious() {
        if (this.currentTrack > 0) {
            this.selectTrack(this.currentTrack - 1, 0);
            this.play();
        }
    }

    prev10() {
        this.audio.currentTime -= 10;
    }

    next10() {
        this.audio.currentTime += 10;
    }

    playNext() {
        if (this.currentTrack < this.playlist.count() - 1) {
            this.selectTrack(this.currentTrack + 1, 0);
            this.play();
        }
    }

    reset() {
        if (this.audio) {
            this.audio.src = '';
        }
        this.currentTrack = -1;
        if (this.currentTrackTitle) {
            this.currentTrackTitle.innerText = '';
        }
        const currentTimeElement = document.querySelector(this.elementSelectors.currentTimeSelector);
        if (currentTimeElement) {
            currentTimeElement.innerHTML = Time.format(0);
        }
        const maxTimeElement = document.querySelector(this.elementSelectors.maxTimeSelector);
        if (maxTimeElement) {
            maxTimeElement.innerHTML = Time.format(0);
        }
        const progressbar = document.querySelector(this.elementSelectors.progressBarSelector);
        if (progressbar) {
            progressbar.style.width = 0 + "%";
        }
        if (this.playlist) {
            this.playlist.reset();
        }
        if (this.playButton) {
            this.playButton.removeEventListener('click', this.playPauseFunction);
            this.playButton.innerText = 'Play';
        }
    }

    console(str) {
        if (this.DEBUG) {
            this.console(str);
        }
    }

}

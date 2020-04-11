/*
 * Copyright (C) 2011-2020 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

"use strict";

import {Time} from "./lib/time.js";
import {Playlist} from "./playlist.js";

export class Audioplayer {

    constructor() {
        this.audio = this.createAudioElement();
        this.initElementSelectors();
    }

    init(audiobookURL, onReadyCallback) {
        this.audiobookURL = audiobookURL;
        this.playButton = document.querySelector(this.elementSelectors.playButtonSelector);
        this.currentTrackTitle = document.querySelector(this.elementSelectors.currentTrackTitleSelector);
        this.displayAudiobookInfo();
        this.playlist = new Playlist(this, this.audiobookURL, () => {
            this.currentTrack = -1;
            this.initAudioEvents();
            this.initPlayerControls();
            onReadyCallback();
        });
    }

    createAudioElement() {
        const audio = document.createElement('audio');
        audio.id = 'audio';
        return audio;
    }

    initElementSelectors() {
        this.elementSelectors = {
            'titleTextSelector': 'div.titel > span',
            'titelnummerTextSelector': 'div.titelnummer > span',
            'authorTextSelector': 'div.author > span',
            'narratorTextSelector': 'div.narrator > span',
            'currentTrackTitleSelector': 'div.title > span',
            'currentTimeSelector': '#currentTime',
            'maxTimeSelector': '#maxTime',
            'progressBarSelector': 'div.progressbar',
            'previousTrackButtonSelector': 'button.previousTrack',
            'prev10TrackButtonSelector': 'button.prev10',
            'playButtonSelector': 'button.play',
            'next10TrackButtonSelector': 'button.next10',
            'nextTrackButtonSelector': 'button.nextTrack'
        };
    }

    displayAudiobookInfo() {
        fetch(new URL('info', this.audiobookURL).toString())
            .then(response => {
                return response.json();
            })
            .then(json => {
                document.querySelector(this.elementSelectors.titleTextSelector).innerHTML = json.titel;
                document.querySelector(this.elementSelectors.titelnummerTextSelector).innerHTML = json.titelnummer;
                document.querySelector(this.elementSelectors.authorTextSelector).innerHTML = json.autor;
                document.querySelector(this.elementSelectors.narratorTextSelector).innerHTML = json.sprecher;
            })
            .catch(reason => alert('initSoundService 1: ' + reason));
    }

    initAudioEvents() {
        //this.audio.canPlayType('audio/mp3')
        const currentTimeElement = document.querySelector(this.elementSelectors.currentTimeSelector);
        const maxTimeElement = document.querySelector(this.elementSelectors.maxTimeSelector);
        const progressbar = document.querySelector(this.elementSelectors.progressBarSelector);
        this.audio.addEventListener('timeupdate', event => {
            const currentTime = parseInt(this.audio.currentTime, 10);
            currentTimeElement.innerHTML = Time.format(currentTime);
            const pct = (this.audio.currentTime / this.audio.duration) * 100;
            progressbar.style.width = parseInt(pct, 10) + "%";
        });
        this.audio.addEventListener('canplay', event => {
            maxTimeElement.innerHTML = Time.format(parseInt(this.audio.duration, 10));
        });
        /*
        this.audio.addEventListener('canplaythrough', event => {
            console.log('canplaythrough');
        });
        */
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

    resumeAudioContext() {
        if (undefined === this.audioContext) {
            const AudioContext = window.AudioContext || window.webkitAudioContext;
            this.audioContext = new AudioContext();
        }
        if (this.audioContext.state === 'suspended') {
            this.audioContext
                .resume()
                .then(() => {
                    console.log('runAudioContext: Playback resumed successfully');
                    this.selectTrack(this.currentTrack, 0);
                    this.play();
                });
        }
    }

    playPauseFunction = event => {
        const elt = event.target;
        switch (elt.innerText) {
            case 'Play':
                if (this.currentTrack === -1) {
                    this.selectTrack(0, 0);
                }
                this.resumeAudioContext();
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
            const id = this.playlist.track(this.currentTrack).playlistElementId;
            document.querySelector('#' + id).classList.remove('currently-playing');
        }
        trackIndex = parseInt(trackIndex);
        const track = this.playlist.track(trackIndex);
        this.currentTrackTitle.innerText = track.title || track.ident;
        const trackId = track.playlistElementId;
        document.querySelector('#' + trackId).classList.add('currently-playing');
        this.audio.src = new URL('track/' + track.ident, this.audiobookURL).toString();
        this.audio.load();
        if (currentSecs > 0) {
            this.audio.currentTime = parseFloat(currentSecs);
        }
        this.currentTrack = trackIndex;
    }

    play() {
        if (this.audioContext) {
            if (this.audioContext.state !== 'suspended') {
                const promise = this.audio.play();
                if (undefined !== promise) {
                    promise.then(_ => {
                        this.playButton.innerText = 'Pause';
                    }).catch(error => {
                        console.log('play(): audio.play() was prevented: ' + error);
                    });
                } else {
                    console.log('play(): No promise');
                }
            } else {
                console.log('play(): AudioContext suspended');
            }
        } else {
            console.log('play(): No AudioContext');
        }
    }

    pause() {
        this.audio.pause();
        this.playButton.innerText = 'Play';
    }

    playPrevious() {
        if (this.currentTrack > 0) {
            this.selectTrack(this.currentTrack - 1, 0);
            // TODO Call play() if current state == playing?
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
            // TODO Call play() if current state == playing?
            this.play();
        }
    }

    reset() {
        if (this.audio) {
            this.audio.src = '';
        }
        this.currentTrack = -1;
        if (this.currentTrackTitle) this.currentTrackTitle.innerText = '';
        const currentTimeElement = document.querySelector(this.elementSelectors.currentTimeSelector);
        if (currentTimeElement) currentTimeElement.innerHTML = Time.format(0);
        const maxTimeElement = document.querySelector(this.elementSelectors.maxTimeSelector);
        if (maxTimeElement) maxTimeElement.innerHTML = Time.format(0);
        const progressbar = document.querySelector(this.elementSelectors.progressBarSelector);
        if (progressbar) progressbar.style.width = 0 + "%";
        let playlistElement = document.querySelector('.playlist ul');
        if (playlistElement) playlistElement.remove();
        if (this.playButton) {
            this.playButton.removeEventListener('click', this.playPauseFunction);
            this.playButton.innerText = 'Play';
        }
    }

}

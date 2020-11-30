/*
 * Copyright (C) 2019-2020 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

"use strict";

import {Time} from "./time.js";
import {FetchErrorHandler} from "./fetchErrorHandler.js";
import {Playlist} from "./playlist.js";
import {VolumeControl} from "./volumeControl.js";

export class Audioplayer {

    constructor() {
        this.audio = this.createAudioElement();
        this.initElementSelectors();
        this.DEBUG = true;
        this.asyncDownloadStatusTimeoutId = new Map();
    }

    init(audiobookURL, mandant, hoerernummer, titelnummer, onReadyCallback) {
        this.audiobookURL = audiobookURL;
        this.mandant = mandant;
        this.hoerernummer = hoerernummer;
        this.titelnummer = titelnummer;
        this.playButton = document.querySelector(this.elementSelectors.playButtonSelector);
        this.currentTrackTitle = document.querySelector(this.elementSelectors.currentTrackTitleSelector);
        this.displayAudiobookInfo();
        this.playlist = new Playlist(this.elementSelectors, this,
            this.audiobookURL,
            this.mandant, this.hoerernummer, this.titelnummer,
            () => {
                this.currentTrack = -1;
                this.initAudioEvents();
                this.initPlayerControls();
                this.volumeControl = new VolumeControl(this.elementSelectors, this.audio);
                onReadyCallback();
            });
        this.downloadStatusText = document.querySelector(this.elementSelectors.downloadStatusTextSelector);
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
            'volumeSliderInputSelector': 'input.volumeSlider',
            'downloadStatusTextSelector': '#downloadStatus'
        };
    }

    createAudioElement() {
        const audio = document.createElement('audio');
        audio.id = 'audio';
        audio.load();
        return audio;
    }

    displayAudiobookInfo() {
        fetch(new URL('v1/katalog/' + this.titelnummer, this.audiobookURL).toString(),
            {
                'method': 'GET',
                'headers': {
                    'Accept': 'application/json',
                    'X-Bookworm-Mandant': this.mandant,
                    'X-Bookworm-Hoerernummer': this.hoerernummer
                }
            })
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
                if (this.DEBUG) {
                    console.log('displayAudiobookInfo,fetch,catch: ' + reason);
                }
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
        this.audio.addEventListener('canplay', event => {
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
        if (this.DEBUG) {
            console.log('selectTrack(' + trackIndex + ')');
        }
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
        const init1 = {
            'method': 'GET',
            'headers': {
                'Accept': 'application/json',
                'X-Bookworm-Mandant': '06',
                'X-Bookworm-Hoerernummer': this.hoerernummer
            }
        };
        fetch(new URL('v1/katalog/' + this.titelnummer + '/track/' + track.ident, this.audiobookURL).toString(), init1)
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
                if (this.DEBUG) {
                    console.log('play,fetch,catch: ' + reason);
                }
            });
        const trackId = track.playlistElementId;
        document.querySelector('#' + trackId).classList.add('currently-playing');
        const url = new URL('v1/stream/' + this.titelnummer + '/track/' + track.ident, this.audiobookURL);
        fetch(url.toString(), init1)
            .then(response => {
                if (this.DEBUG) {
                    console.log('selectTrack: GET audio.src=' + url.toString());
                }
                return response.blob();
            })
            .then(blob => {
                //this.audio.srcObject = blob;
                this.audio.src = URL.createObjectURL(blob);
                if (this.DEBUG) {
                    console.log('this.audio.src=' + this.audio.src);
                }
                if (this.DEBUG) {
                    console.log('selectTrack: audio.load()');
                }
                this.audio.load();
                if (currentSecs > 0) {
                    this.audio.currentTime = parseFloat(currentSecs);
                }
                this.currentTrack = trackIndex;
                this.play();
            })
    }

    play() {
        if (this.DEBUG) {
            console.log('play()');
        }
        const playPromise = this.audio.play();
        if (undefined !== playPromise) {
            playPromise.then(param => {
                this.playButton.innerText = 'Pause';
                if (this.DEBUG) {
                    console.log('play(): promise ended, param=' + param);
                }
            }).catch(error => {
                if (this.DEBUG) {
                    console.log('play(): audio.play() was prevented: ' + error);
                }
            });
        } else {
            if (this.DEBUG) {
                console.log('play(): No promise');
            }
        }
    }

    pause() {
        if (this.DEBUG) {
            console.log('pause()');
        }
        this.audio.pause();
        this.playButton.innerText = 'Play';
    }

    playPrevious() {
        if (this.DEBUG) {
            console.log('playPrevious()');
        }
        this.pause();
        if (this.currentTrack > 0) {
            this.selectTrack(this.currentTrack - 1, 0);
        }
    }

    prev10() {
        this.audio.currentTime -= 10;
    }

    next10() {
        this.audio.currentTime += 10;
    }

    playNext() {
        if (this.DEBUG) {
            console.log('playNext()');
        }
        this.pause();
        if (this.currentTrack < this.playlist.count() - 1) {
            this.selectTrack(this.currentTrack + 1, 0);
        }
    }

    syncDownload() {
        this.downloadStatusText.innerHTML = 'DAISY Hörbuch wird heruntergeladen';
        fetch(new URL('v1/hoerbuch/' + this.titelnummer, this.audiobookURL).toString(),
            {
                'method': 'POST',
                'headers': {
                    'Accept': 'application/zip',
                    'Content-Type': 'application/json',
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
                if (blob) {
                    const a = document.createElement('a');
                    a.href = window.URL.createObjectURL(blob);
                    a.download = this.titelnummer + '.zip';
                    a.click();
                } else {
                    console.log('download(): Sorry, no blob');
                }
            })
            .catch(reason => {
                if (this.DEBUG) {
                    console.log('syncDownload: ' + reason);
                }
            });
    }

    asyncDownloadOrder() {
        const url = new URL('v1/bestellung/' + this.titelnummer, this.audiobookURL).toString();
        fetch(url, {
            'method': 'POST',
            'headers': {
                'Accept': 'application/json',
                'Content-Type': 'application/json',
                'X-Bookworm-Mandant': this.mandant,
                'X-Bookworm-Hoerernummer': this.hoerernummer
            },
            'redirect': 'follow'
        })
            .then(response => {
                if (response.ok) {
                    return response.json();
                } else {
                    FetchErrorHandler.handle(response);
                }
            })
            .then(json => {
                if (json) {
                    this.orderId = json.orderId;
                    this.downloadStatusText.innerHTML = 'Bestellung ' + this.orderId + ' aufgegeben';
                    console.log('asyncDownloadOrder(): orderId ' + this.orderId);
                    this.asyncDownloadStatus();
                } else {
                    console.log('asyncDownloadOrder(): Sorry, no JSON');
                }
            })
            .catch(reason => {
                if (this.DEBUG) {
                    console.log('asyncDownloadOrder(): ' + reason);
                }
            });
    }

    asyncDownloadStatus() {
        this.downloadStatusText.innerHTML = 'DAISY Hörbuch wird erzeugt...';
        let orderStatus = "";
        const url = new URL('v1/bestellung/' + this.titelnummer + '/status/' + this.orderId, this.audiobookURL).toString();
        fetch(url, {
            'method': 'GET',
            'headers': {
                'Accept': 'application/json',
                'X-Bookworm-Mandant': this.mandant,
                'X-Bookworm-Hoerernummer': this.hoerernummer
            },
            'redirect': 'follow'
        })
            .then(response => {
                if (response.ok) {
                    return response.json();
                } else {
                    FetchErrorHandler.handle(response);
                }
            })
            .then(json => {
                if (json && json.orderStatus) {
                    orderStatus = json.orderStatus;
                    console.log('asyncDownloadOrder(): Order status ' + this.orderId + ' is ' + orderStatus);
                    if (orderStatus === 'SUCCESS' || orderStatus === 'FAILED') {
                        switch (orderStatus) {
                            case 'SUCCESS':
                                this.downloadStatusText.innerHTML = 'Bestellung erfolgreich';
                                this.asyncDownloadAudiobook();
                                break;
                            case 'FAILED':
                                this.downloadStatusText.innerHTML = 'Bestellung fehlerhaft!';
                                break;
                            default:
                                alert('Unbekannter Status!');
                        }
                        if (this.asyncDownloadStatusTimeoutId.has(this.orderId)) {
                            clearTimeout(this.asyncDownloadStatusTimeoutId.get(this.orderId));
                            this.asyncDownloadStatusTimeoutId.delete(this.orderId);
                        }
                    } else {
                        this.asyncDownloadStatusTimeoutId.set(this.orderId,
                            setTimeout(() => this.asyncDownloadStatus(), 1500));
                    }
                } else {
                    this.downloadStatusText.innerHTML = 'Keine Daten!';
                    console.log('asyncDownloadStatus(): Sorry, no JSON');
                }
            })
            .catch(reason => {
                if (this.DEBUG) {
                    console.log('asyncDownloadStatus(): Cannot retrieve orderStatus: ' + reason);
                }
            });
    }

    asyncDownloadAudiobook() {
        const url = new URL('v1/bestellung/' + this.titelnummer + '/fetch/' + this.orderId, this.audiobookURL).toString();
        this.downloadStatusText.innerHTML = 'DAISY Hörbuch wird heruntergeladen!';
        fetch(url, {
            'method': 'GET',
            'headers': {
                'Accept': 'application/zip',
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
                this.downloadStatusText.innerHTML = 'DAISY Hörbuch wird im Downloads-Ordner gespeichert!';
                const url = window.URL.createObjectURL(blob);
                const a = document.createElement('a');
                a.href = url;
                a.download = this.titelnummer + ".zip";
                document.body.appendChild(a);
                a.click();
                a.remove();
                this.orderId = null;
            })
            .catch(reason => {
                if (this.DEBUG) {
                    console.log('asyncDownloadAudiobook(): ' + reason);
                }
            });
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
        const trackInfoText = document.querySelector(this.elementSelectors.trackInfoTextSelector);
        if (trackInfoText) {
            trackInfoText.innerHTML = '&nbsp;';
        }
    }

}

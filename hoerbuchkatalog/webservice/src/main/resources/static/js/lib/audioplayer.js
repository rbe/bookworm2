/*
 * Copyright (C) 2019-2020 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

"use strict";

import {FetchErrorHandler} from "./fetchErrorHandler.js";

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
            this.audiobookURL, this.hoerernummer, this.titelnummer,
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

    asyncDownloadOrder() {
        const url = new URL('v1/bestellung/' + this.titelnummer, this.audiobookURL).toString();
        fetch(url, {
            'method': 'POST',
            'mode': 'cors',
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
            'mode': 'cors',
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
            'mode': 'cors',
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

}

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
        this.audio = this.createAudioElement();
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
                return response.blob();
            })
            .then(blob => {
                this.audio.src = URL.createObjectURL(blob);
                this.audio.load();
            })
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

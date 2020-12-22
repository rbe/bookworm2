/*
 * Copyright (C) 2020 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

"use strict";

import {FetchErrorHandler} from "./fetchErrorHandler.js";

const HOERBUCHKATALOG_URL = 'https://www.prod.wbh-online.de';

const SHARD_URLS = [
    'https://hoerbuchdienst-shard11.audiobook.wbh-online.de',
    'https://hoerbuchdienst-shard12.audiobook.wbh-online.de',
    'https://hoerbuchdienst-shard13.audiobook.wbh-online.de'
];
const DOWNLOAD_STATUS_TIMEOUT = 2500;

export class BookwormRestClient {

    constructor(mandant, hoerernummer, bestellungSessionId) {
        this.mandant = mandant;
        this.hoerernummer = hoerernummer;
        this.bestellungSessionId = bestellungSessionId;
        this.asyncDownloadStatusTimeoutId = new Map();
    }

    //
    // Merkliste
    //

    fuegeZuMerklisteHinzu(titelnummer, successCallback) {
        const url = new URL('/hoerbuchkatalog/v1/merkliste/' + titelnummer, HOERBUCHKATALOG_URL);
        fetch(url.toString(), {
            'method': 'PUT',
            'headers': {
                'Accept': 'application/json',
                'Content-Type': 'application/json',
                'X-Bookworm-Mandant': this.mandant,
                'X-Bookworm-Hoerernummer': this.hoerernummer,
                'X-Bookworm-BestellungSessionId': this.bestellungSessionId
            }
        })
            .then(response => {
                if (response.ok && undefined !== successCallback) {
                    successCallback();
                } else {
                    FetchErrorHandler.handle(response);
                }
            })
            .catch(reason => {
                console.log('Fehler: ' + reason);
            });
    }

    entferneVonMerkliste(titelnummer, successCallback) {
        const url = new URL('/hoerbuchkatalog/v1/merkliste/' + titelnummer, HOERBUCHKATALOG_URL);
        fetch(url.toString(), {
            'method': 'DELETE',
            'headers': {
                'Accept': 'application/json',
                'X-Bookworm-Mandant': this.mandant,
                'X-Bookworm-Hoerernummer': this.hoerernummer,
                'X-Bookworm-BestellungSessionId': this.bestellungSessionId
            }
        })
            .then(response => {
                if (response.ok && undefined !== successCallback) {
                    successCallback();
                } else {
                    FetchErrorHandler.handle(response);
                }
            })
            .catch(reason => {
                console.log('Fehler: ' + reason);
            });
    }

    //
    // Warenkorb
    //

    fuegeZuWarenkorbHinzu(titelnummer, successCallback) {
        const url = new URL('/hoerbuchkatalog/v1/warenkorb/' + titelnummer, HOERBUCHKATALOG_URL);
        fetch(url.toString(), {
            'method': 'PUT',
            'headers': {
                'Accept': 'application/json',
                'Content-Type': 'application/json',
                'X-Bookworm-Mandant': this.mandant,
                'X-Bookworm-Hoerernummer': this.hoerernummer,
                'X-Bookworm-BestellungSessionId': this.bestellungSessionId,
            }
        })
            .then(response => {
                if (response.ok && undefined !== successCallback) {
                    successCallback();
                } else {
                    FetchErrorHandler.handle(response);
                }
            })
            .catch(reason => {
                console.log('Fehler: ' + reason);
            });
    }

    entferneAusWarenkorb(titelnummer, successCallback) {
        const url = new URL('/hoerbuchkatalog/v1/warenkorb/' + titelnummer, HOERBUCHKATALOG_URL);
        fetch(url.toString(), {
            'method': 'DELETE',
            'headers': {
                'Accept': 'application/json',
                'X-Bookworm-Mandant': this.mandant,
                'X-Bookworm-Hoerernummer': this.hoerernummer,
                'X-Bookworm-BestellungSessionId': this.bestellungSessionId,
            }
        })
            .then(response => {
                if (response.ok && undefined !== successCallback) {
                    successCallback();
                } else {
                    FetchErrorHandler.handle(response);
                }
            })
            .catch(reason => {
                console.log('Fehler: ' + reason);
            });
    }

    //
    // Hörprobe
    //

    bestelleHoerprobe(titelnummer, playCallback) {
        this.shardLocation(titelnummer)
            .then(shardName => {
                const url = new URL('v1/hoerprobe/' + titelnummer, 'https://' + shardName);
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
                        if (response.ok) { // Ok, kann aber leer sein!
                            return response.blob();
                        } else {
                            FetchErrorHandler.handle(response);
                        }
                    })
                    .then(blob => {
                        if (undefined !== blob && blob.size > 0) {
                            playCallback(blob);
                        }
                    })
                    .catch(reason => {
                        console.log('Fehler: ' + reason);
                    });
            })
            .catch(reason => {
                console.log('Fehler: ' + reason);
            });
    }

    //
    // Download
    //

    bestelleDownload(titelnummer, element, downloadFertigCallback) {
        this.shardLocation(titelnummer)
            .then(shardName => {
                const url = new URL('v1/bestellung/' + titelnummer, 'https://' + shardName);
                fetch(url.toString(), {
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
                            // TODO HTTP 404 Buch nicht gefunden (Databeat noch nicht vollständig)
                            FetchErrorHandler.handle(response);
                        }
                    })
                    .then(json => {
                        if (json && json.orderId) {
                            console.log('orderId ist ' + json.orderId);
                            this.warteAufDownload(shardName, titelnummer, json.orderId, element, downloadFertigCallback);
                        } else {
                            console.log('Kein JSON oder keine orderId bekommen');
                        }
                    })
                    .catch(reason => {
                        console.log('Fehler: ' + reason);
                        if (undefined !== downloadFertigCallback) {
                            downloadFertigCallback(element);
                        }
                    });
            })
            .catch(reason => {
                console.log('Fehler: ' + reason);
                if (undefined !== downloadFertigCallback) {
                    downloadFertigCallback(element);
                }
            });
    }

    warteAufDownload(shardName, titelnummer, orderId, element, callback) {
        const url = new URL('v1/bestellung/' + titelnummer + '/status/' + orderId, 'https://' + shardName);
        fetch(url.toString(), {
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
                    const orderStatus = json.orderStatus;
                    console.log(orderId + ': Status ist ' + orderStatus);
                    if (orderStatus === 'SUCCESS' || orderStatus === 'FAILED') {
                        switch (orderStatus) {
                            case 'SUCCESS':
                                this.downloadHoerbuch(shardName, titelnummer, orderId, element, callback);
                                break;
                            case 'FAILED':
                                alert('Entschuldigung, das Hörbuch konnte nicht bereitgestellt werden');
                                break;
                            default:
                                alert('Unbekannter Status!');
                        }
                        if (this.asyncDownloadStatusTimeoutId.has(orderId)) {
                            clearTimeout(this.asyncDownloadStatusTimeoutId.get(orderId));
                            this.asyncDownloadStatusTimeoutId.delete(orderId);
                        }
                    } else {
                        this.asyncDownloadStatusTimeoutId.set(orderId,
                            setTimeout(() => this.warteAufDownload(shardName, titelnummer, orderId, element, callback), DOWNLOAD_STATUS_TIMEOUT));
                    }
                }
            })
            .catch(reason => {
                console.log('Fehler: ' + reason);
                if (undefined !== callback) {
                    callback(element);
                }
            });
    }

    downloadHoerbuch(shardName, titelnummer, orderId, element, callback) {
        if (undefined !== callback) {
            callback(element);
        }
        const url = new URL('v1/bestellung/' + titelnummer + '/fetch/' + orderId
            + '/' + this.mandant + '/' + this.hoerernummer, 'https://' + shardName);
        //const newWindow = window.open(url, 'daisyHoerbuchDownload');
        const anchor = document.createElement('a');
        anchor.href = url.toString();
        anchor.download = titelnummer + '.zip';
        document.body.appendChild(anchor);
        anchor.click();
        anchor.remove();
    }

    //
    // Shards
    //

    shardLocation(titelnummer) {
        function getRandomInt(max) {
            return Math.floor(Math.random() * Math.floor(max));
        }

        const shardUrl = SHARD_URLS[getRandomInt(SHARD_URLS.length - 1)];
        return fetch(shardUrl + '/v1/shard/location/' + titelnummer,
            {'method': 'GET', 'mode': 'cors'})
            .then(response => response.text())
            .catch(reason => {
                console.log('Fehler: ' + reason);
            });
    }

}

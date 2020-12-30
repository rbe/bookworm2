/*
 * Copyright (C) 2020 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

"use strict";

import {FetchErrorHandler} from "./fetchErrorHandler.js";
import {DOWNLOAD_STATUS_TIMEOUT, HOERBUCHKATALOG_URL, SHARD_URLS} from "./wbhonlineHelper.js";

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
                'X-Bookworm-Mandant': this.mandant,
                'X-Bookworm-Hoerernummer': this.hoerernummer,
                'X-Bookworm-BestellungSessionId': this.bestellungSessionId
            }
        })
            .then(response => {
                if (response.ok && undefined !== successCallback && null !== successCallback) {
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
                if (response.ok && undefined !== successCallback && null !== successCallback) {
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
                'X-Bookworm-Mandant': this.mandant,
                'X-Bookworm-Hoerernummer': this.hoerernummer,
                'X-Bookworm-BestellungSessionId': this.bestellungSessionId,
            }
        })
            .then(response => {
                if (response.ok && undefined !== successCallback && null !== successCallback) {
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
                if (response.ok && undefined !== successCallback && null !== successCallback) {
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
                        if (undefined !== blob && null !== blob && blob.size > 0) {
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

    downloadErlaubt(titelnummer) {
        const url = new URL('/hoerbuchkatalog/v1/downloads/erlaubt', HOERBUCHKATALOG_URL);
        return fetch(url.toString(), {'method': 'GET'})
            .then(response => response.ok)
            .catch(reason => {
                console.log('Fehler: ' + reason);
                return false;
            });
    }

    bestelleDownload(titelnummer, element, downloadFertigCallback) {
        this.downloadErlaubt(titelnummer)
            .then(ok => {
                if (ok) {
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
                                    if (undefined !== downloadFertigCallback && null !== downloadFertigCallback) {
                                        downloadFertigCallback(element);
                                    }
                                });
                        })
                        .catch(reason => {
                            console.log('Fehler: ' + reason);
                            if (undefined !== downloadFertigCallback&& null !== downloadFertigCallback) {
                                downloadFertigCallback(element);
                            }
                        });
                } else {
                    console.log('Download ' + titelnummer + ' ist nicht erlaubt');
                }
            })
            .catch(reason => {
                console.log('Fehler: ' + reason);
                if (undefined !== downloadFertigCallback && null !== downloadFertigCallback) {
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
                if (undefined !== callback && null !== callback) {
                    callback(element);
                }
            });
    }

    downloadHoerbuch(shardName, titelnummer, orderId, element, callback) {
        if (undefined !== callback && null !== callback) {
            callback(element);
        }
        const url = new URL('v1/bestellung/' + titelnummer + '/fetch/' + orderId, 'https://' + shardName);
        //const newWindow = window.open(url, 'daisyHoerbuchDownload');
        const anchor = document.createElement('a');
        anchor.href = url.toString();
        anchor.download = titelnummer + '.zip';
        document.body.appendChild(anchor);
        anchor.click();
        anchor.remove();
        this.verbucheDownload(shardName, titelnummer);
    }

    verbucheDownload(shardName, titelnummer) {
        const url = new URL('/hoerbuchkatalog/v1/downloads/' + titelnummer, HOERBUCHKATALOG_URL);
        fetch(url.toString(), {
            'method': 'PUT',
            'headers': {
                'Accept': 'application/json',
                'X-Bookworm-Mandant': this.mandant,
                'X-Bookworm-Hoerernummer': this.hoerernummer,
                'X-Bookworm-BestellungSessionId': this.bestellungSessionId
            }
        })
            .then(response => {
                if (response.ok) {
                    // ignore
                } else {
                    FetchErrorHandler.handle(response);
                }
            })
            .catch(reason => {
                console.log('Fehler: ' + reason);
            });
    }

    //
    // Shards
    //

    shardLocation(titelnummer) {
        const shardUrl = this.randomShard();
        const url = new URL('/v1/shard/location/' + titelnummer, shardUrl);
        return fetch(url.toString(), {'method': 'GET', 'mode': 'cors'})
            .then(response => response.text())
            .catch(reason => {
                console.log('Fehler: ' + reason);
            });
    }

    randomShard() {
        return SHARD_URLS[this.getRandomInt(SHARD_URLS.length - 1)];
    }

    getRandomInt(max) {
        return Math.floor(Math.random() * Math.floor(max));
    }

}

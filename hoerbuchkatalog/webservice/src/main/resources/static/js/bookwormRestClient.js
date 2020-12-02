/*
 * Copyright (C) 2020 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

"use strict";

import {Audioplayer} from "./audioplayer.js";
import {FetchErrorHandler} from "./fetchErrorHandler.js";

const HOERBUCHKATALOG_URL = 'https://www.beta.wbh-online.de';
const SHARD_URL = 'https://hoerbuchdienst.shard4.audiobook.wbh-online.de';
const DOWNLOAD_STATUS_TIMEOUT = 2500;

export class BookwormRestClient {

    constructor(mandant, hoerernummer) {
        this.mandant = mandant;
        this.hoerernummer = hoerernummer;
        this.audioplayer = new Audioplayer(SHARD_URL, mandant, hoerernummer);
        this.asyncDownloadStatusTimeoutId = new Map();
        this.orderId = '';
    }

    fuegeZuMerklisteHinzu(titelnummer, successCallback) {
        const url = new URL('/hoerbuchkatalog/v1/merkliste/' + titelnummer, HOERBUCHKATALOG_URL);
        fetch(url.toString(), {
            'method': 'POST',
            'headers': {
                'Accept': 'application/json',
                'Content-Type': 'application/json',
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
                if (json.result === true && successCallback) {
                    successCallback();
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
                if (json.result === true && successCallback) {
                    successCallback();
                }
            })
            .catch(reason => {
                console.log('Fehler: ' + reason);
            });
    }

    fuegeZuWarenkorbHinzu(titelnummer, successCallback) {
        const url = new URL('/hoerbuchkatalog/v1/warenkorb/' + titelnummer, HOERBUCHKATALOG_URL);
        fetch(url.toString(), {
            'method': 'POST',
            'headers': {
                'Accept': 'application/json',
                'Content-Type': 'application/json',
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
                if (json.result === true && successCallback) {
                    successCallback();
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
                if (json.result === true && successCallback) {
                    successCallback();
                }
            })
            .catch(reason => {
                console.log('Fehler: ' + reason);
            });
    }

    hoerprobe(titelnummer) {
        this.audioplayer.hoerprobe(titelnummer);
    }

    bestelleDownload(titelnummer, successCallback) {
        const url = new URL('v1/bestellung/' + titelnummer, SHARD_URL).toString();
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
                    console.log('orderId ist ' + json.orderId);
                    this.warteAufDownload(titelnummer, json.orderId);
                } else {
                    console.log('asyncDownloadOrder(): Sorry, no JSON');
                }
            })
            .catch(reason => {
                console.log('Fehler: ' + reason);
            });
    }

    warteAufDownload(titelnummer, orderId) {
        const url = new URL('v1/bestellung/' + titelnummer + '/status/' + orderId, SHARD_URL);
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
                    console.log(this.orderId + ': Status ist ' + orderStatus);
                    if (orderStatus === 'SUCCESS' || orderStatus === 'FAILED') {
                        switch (orderStatus) {
                            case 'SUCCESS':
                                this.downloadHoerbuch(titelnummer, orderId);
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
                            setTimeout(() => this.warteAufDownload(titelnummer, orderId), DOWNLOAD_STATUS_TIMEOUT));
                    }
                }
            })
            .catch(reason => {
                console.log('Fehler: ' + reason);
            });
    }

    downloadHoerbuch(titelnummer, orderId) {
        const url = new URL('v1/bestellung/' + titelnummer + '/fetch/' + orderId, SHARD_URL);
        // X-Bookworm-Header fehlen const newWindow = window.open(url, 'daisyHoerbuchDownload');
        const anchor = document.createElement('a');
        anchor.href = url.toString();
        anchor.download = titelnummer + '.zip';
        document.body.appendChild(anchor);
        anchor.click();
        anchor.remove();
    }

}

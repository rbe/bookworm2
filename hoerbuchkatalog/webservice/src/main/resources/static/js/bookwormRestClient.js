/*
 * Copyright (C) 2020 art of coding UG, https://www.art-of-coding.eu
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

"use strict";

import {Audioplayer} from "./audioplayer.js";
import {FetchErrorHandler} from "./fetchErrorHandler.js";

const hoerbuchkatalogURL = 'https://www.beta.wbh-online.de';
const shardURL = 'https://hoerbuchdienst.shard4.audiobook.wbh-online.de';

export class BookwormRestClient {

    constructor(mandant, hoerernummer) {
        this.mandant = mandant;
        this.hoerernummer = hoerernummer;
        this.audioplayer = new Audioplayer(shardURL, mandant, hoerernummer);
        this.orderId = '';
    }

    fuegeZuMerklisteHinzu(titelnummer, successCallback) {
        const url = new URL('/hoerbuchkatalog/v1/merkliste/' + titelnummer, hoerbuchkatalogURL);
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
                if (json.result === 'true' && successCallback) {
                    successCallback();
                }
            })
            .catch(reason => {
                console.log('Fehler: ' + reason);
            });
    }

    entferneVonMerkliste(titelnummer, successCallback) {
        const url = new URL('/hoerbuchkatalog/v1/merkliste/' + titelnummer, hoerbuchkatalogURL);
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
                if (json.result === 'true' && successCallback) {
                    successCallback();
                }
            })
            .catch(reason => {
                console.log('Fehler: ' + reason);
            });
    }

    fuegeZuWarenkorbHinzu(titelnummer, successCallback) {
        const url = new URL('/hoerbuchkatalog/v1/warenkorb/' + titelnummer, hoerbuchkatalogURL);
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
                if (json.result === 'true' && successCallback) {
                    successCallback();
                }
            })
            .catch(reason => {
                console.log('Fehler: ' + reason);
            });
    }

    entferneAusWarenkorb(titelnummer, successCallback) {
        const url = new URL('/hoerbuchkatalog/v1/warenkorb/' + titelnummer, hoerbuchkatalogURL);
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
                if (json.result === 'true' && successCallback) {
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
        const url = new URL('v1/bestellung/' + titelnummer, shardURL).toString();
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
                    console.log('orderId ist ' + this.orderId);
                    this.warteAufDownload(titelnummer);
                } else {
                    console.log('asyncDownloadOrder(): Sorry, no JSON');
                }
            })
            .catch(reason => {
                console.log('Fehler: ' + reason);
            });
    }

    warteAufDownload(titelnummer) {
        const url = new URL('v1/bestellung/' + titelnummer + '/status/' + this.orderId, shardURL);
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
                                this.asyncDownloadAudiobook();
                                break;
                            case 'FAILED':
                                alert('Entschuldigung, die Bestellung konnte nicht bearbeitet werden');
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
                            setTimeout(() => this.warteAufDownload(), 1500));
                    }
                } else {
                    console.log('asyncDownloadStatus(): Sorry, no JSON');
                }
            })
            .catch(reason => {
                console.log('Fehler: ' + reason);
            });
    }


}

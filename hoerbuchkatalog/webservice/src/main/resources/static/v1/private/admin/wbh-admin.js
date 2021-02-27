"use strict";

function idpUrl() {
    if (window.location.hostname === 'localhost') {
        return 'http://localhost:8081';
    } else {
        const host = window.location.host.split('.');
        host.splice(0, 1, 'idp');
        return window.location.protocol + '//' + host.join('.');
    }
}

function initKeycloak() {
    var keycloak = new Keycloak({
        url: idpUrl() + '/auth',
        realm: 'dev',
        clientId: 'microservice'
    });
    keycloak.init({
        onLoad: 'login-required'
    }).then(function (authenticated) {
        alert(authenticated ? 'authenticated' : 'not authenticated');
    }).catch(function () {
        alert('failed to initialize');
    });
}

document.addEventListener('DOMContentLoaded', (event) => {
    //initKeycloak();
});

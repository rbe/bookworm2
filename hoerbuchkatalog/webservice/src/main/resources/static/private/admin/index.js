"use strict";

document.addEventListener('DOMContentLoaded', (event) => {
    const hoerernummer = document.querySelector('form#abfrage input#hoerernummer');
    const submitButton = document.querySelector('form#kontingent input[type="submit"]');
    const kontingentHoerernummer = document.querySelector('form#kontingent input#hoerernummer');
    submitButton.addEventListener('click', function (e) {
        e.preventDefault();
        kontingentHoerernummer.value = hoerernummer.value;
        document.querySelector('form#kontingent').submit();
    });
});

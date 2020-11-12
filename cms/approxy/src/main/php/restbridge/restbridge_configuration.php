<?php

const REQUEST_DTOS = [
    'AudiobookInfo' => [
        'mandant' => '{mandant}',
        'hoerernummer' => '{hoerernummer}',
        'aghNummer' => '{aghNummer}',
        'titelnummer' => '{titelnummer}'
    ]
];

const REST_ENDPOINTS = [
    'AudiobookInfo' => [
        'url' => 'https://hoerbuchdienst.shard3.audiobook.wbh-online.de',
        'method' => 'POST',
        'parameter_template' => '/info/audiobook',
        'mime_type' => 'application/json',
        'headers' => [
            'Content-Type', 'application/json',
            'Origin', 'audiobook.wbh-online.de'
        ],
        'json_body_template' => REQUEST_DTOS['AudiobookInfo']
    ]
];

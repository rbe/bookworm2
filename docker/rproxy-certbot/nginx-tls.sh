#!/bin/sh
#
# Copyright (C) 2018-2019 art of coding UG, https://www.art-of-coding.eu
# Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
# All rights reserved. Use is subject to license terms.
#

DOMAIN="wbh-online.de"
MAIL="post@medienhof.de"
letsencrypt_path="/etc/letsencrypt/live"
domain_cert_path="${letsencrypt_path}/${DOMAIN}"

mkdir -p ${domain_cert_path} >/dev/null

case "$1" in
    self-signed-test)
        openssl req -x509 -nodes -newkey rsa:1024 -days 365 \
            -keyout ${domain_cert_path}/privkey.pem \
            -out ${domain_cert_path}/fullchain.pem \
            -subj "/CN=${DOMAIN},test.${DOMAIN}"
    ;;
    create-test)
        certbot certonly -n --nginx --agree-tos -m ${MAIL} -d ${DOMAIN},test.${DOMAIN}
    ;;
    self-signed-prod)
        openssl req -x509 -nodes -newkey rsa:1024 -days 365 \
            -keyout ${domain_cert_path}/privkey.pem \
            -out ${domain_cert_path}/fullchain.pem \
            -subj "/CN=${DOMAIN},www.${DOMAIN}"
    ;;
    create-prod)
        certbot certonly -n --nginx --agree-tos -m ${MAIL} -d ${DOMAIN},www.${DOMAIN}
    ;;
    renew)
        certbot certonly -n --nginx renew
    ;;
    *)
        echo "usage: $0 { self-signed-test | create-test | self-signed-prod | create-prod | renew }"
        exit 1
    ;;
esac

exit 0

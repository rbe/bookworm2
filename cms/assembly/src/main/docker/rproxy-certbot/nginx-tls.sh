#!/bin/sh
#
# Copyright (C) 2018-2019 art of coding UG, https://www.art-of-coding.eu
# Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
# All rights reserved. Use is subject to license terms.
#

PROD_DOMAIN="wbh-online.de"
TEST_DOMAIN="test.wbh-online.de"
MAIL="post@medienhof.de"

#
# DO NOT MODIFY LINES BELOW
#

set -o nounset
set -o errexit

SELFSIGNED_PATH="/etc/letsencrypt/selfsigned"
LETSENCRYPT_LIVE_PATH="/etc/letsencrypt/live"
CUSTOM_PATH="/etc/letsencrypt/custom"

case "$1" in
    self-signed-test)
        domain_cert_path="${SELFSIGNED_PATH}/${TEST_DOMAIN}"
        if [ -f ${domain_cert_path}/privkey.pem ] || [ -f ${domain_cert_path}/fullchain.pem ]
        then
            echo "$0: ${domain_cert_path}/privkey.pem or fullchain.pem exists"
            exit 0
        fi
        mkdir -p ${domain_cert_path}
        openssl req -x509 -nodes -newkey rsa:2048 -days 365 \
            -keyout ${domain_cert_path}/privkey.pem \
            -out ${domain_cert_path}/fullchain.pem \
            -subj "/CN=99d9430.online-server.cloud"
        sed -i'' \
            -e "s#ssl_certificate .*#ssl_certificate ${domain_cert_path}/fullchain.pem;#" \
            -e "s#ssl_certificate_key .*#ssl_certificate_key ${domain_cert_path}/privkey.pem;#" \
            /etc/nginx/conf.d/lizenzportal.conf
    ;;
    create-test)
        domain_cert_path="${LETSENCRYPT_LIVE_PATH}/${TEST_DOMAIN}"
        if [ -f ${domain_cert_path}/privkey.pem ] || [ -f ${domain_cert_path}/fullchain.pem ]
        then
            echo "$0: ${domain_cert_path}/privkey.pem or fullchain.pem exists"
            exit 0
        fi
        certbot certonly -n --nginx --agree-tos -m ${MAIL} -d ${TEST_DOMAIN}
        sed -i'' \
            -e "s#ssl_certificate .*#ssl_certificate ${domain_cert_path}/fullchain.pem;#" \
            -e "s#ssl_certificate_key .*#ssl_certificate_key ${domain_cert_path}/privkey.pem;#" \
            -e "s/#ssl_stapling .*/ssl_stapling on;/" \
            -e "s/#ssl_stapling_verify .*/ssl_stapling_verify on;/" \
            /etc/nginx/conf.d/lizenzportal.conf
    ;;
    self-signed-prod)
        domain_cert_path="${SELFSIGNED_PATH}/${PROD_DOMAIN}"
        if [ -f ${domain_cert_path}/privkey.pem ] || [ -f ${domain_cert_path}/fullchain.pem ]
        then
            echo "$0: ${domain_cert_path}/privkey.pem or fullchain.pem exists"
            exit 0
        fi
        mkdir -p ${domain_cert_path}
        openssl req -x509 -nodes -newkey rsa:2048 -days 365 \
            -keyout ${domain_cert_path}/privkey.pem \
            -out ${domain_cert_path}/fullchain.pem \
            -subj "/CN=${DOMAIN},www.${DOMAIN}"
        sed -i'' \
            -e "s#ssl_certificate .*#ssl_certificate ${domain_cert_path}/fullchain.pem;#" \
            -e "s#ssl_certificate_key .*#ssl_certificate_key ${domain_cert_path}/privkey.pem;#" \
            /etc/nginx/conf.d/lizenzportal.conf
    ;;
    create-prod)
        domain_cert_path="${LETSENCRYPT_LIVE_PATH}/${PROD_DOMAIN}"
        if [ -f ${domain_cert_path}/privkey.pem ] || [ -f ${domain_cert_path}/fullchain.pem ]
        then
            echo "$0: ${domain_cert_path}/privkey.pem or fullchain.pem exists"
            exit 0
        fi
        certbot certonly -n --nginx --agree-tos -m ${MAIL} -d ${PROD_DOMAIN},www.${PROD_DOMAIN}
        sed -i'' \
            -e "s#ssl_certificate .*#ssl_certificate ${domain_cert_path}/fullchain.pem;#" \
            -e "s#ssl_certificate_key .*#ssl_certificate_key ${domain_cert_path}/privkey.pem;#" \
            -e "s/#ssl_stapling .*/ssl_stapling on;/" \
            -e "s/#ssl_stapling_verify .*/ssl_stapling_verify on;/" \
            /etc/nginx/conf.d/lizenzportal.conf
    ;;
    renew)
        certbot renew -n --nginx
    ;;
    custom-prod)
        domain_cert_path=/etc/letsencrypt/custom/${PROD_DOMAIN}
        if [ -f ${domain_cert_path}/server.pem ] && [ -f ${domain_cert_path}/intermediate.pem ]
        then
            cat ${domain_cert_path}/server.pem \
                ${domain_cert_path}/intermediate.pem \
                >>${domain_cert_path}/fullchain.pem
            sed -i'' \
                -e "s#ssl_certificate .*#ssl_certificate ${domain_cert_path}/fullchain.pem;#" \
                -e "s#ssl_certificate_key .*#ssl_certificate_key ${domain_cert_path}/privkey.pem;#" \
                -e "s/#ssl_stapling .*/ssl_stapling on;/" \
                -e "s/#ssl_stapling_verify .*/ssl_stapling_verify on;/" \
                /etc/nginx/conf.d/lizenzportal.conf
        fi
        if [ ! -f /etc/letsencrypt/ssl-dhparams.pem ]
        then
            openssl dhparam -out /etc/letsencrypt/ssl-dhparams.pem 4096
            sed -i'' \
                -e "s/#ssl_dhparam (.*);/ssl_dhparam \1;/" \
                /etc/nginx/conf.d/lizenzportal.conf
        fi
    ;;
    *)
        echo "unknown command: $*"
        echo "usage: $0 { self-signed-test | create-test | self-signed-prod | create-prod | renew | custom-prod }"
        exit 1
    ;;
esac

exit 0

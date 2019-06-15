#!/bin/ash

DOMAIN="test.wbh-online.de"
MAIL="post@medienhof.de"
path="/etc/letsencrypt/live/${DOMAIN}"

openssl req -x509 -nodes -newkey rsa:1024 -days 1 \
    -keyout ${path}/privkey.pem \
    -out ${path}/fullchain.pem \
    -subj "/CN=localhost" certbot

certbot -n --nginx --agree-tos -m ${MAIL} -d ${DOMAIN}
#certbot -n --nginx renew

exit 0

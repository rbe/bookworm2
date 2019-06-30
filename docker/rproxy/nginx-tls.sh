#!/bin/ash

DOMAIN="wbh-online.de"
MAIL="post@medienhof.de"
letsencrypt_path="/etc/letsencrypt/live"
domain_cert_path="${letsencrypt_path}/${DOMAIN}"

case "$1" in
    localhost)
        openssl req -x509 -nodes -newkey rsa:1024 -days 365 \
            -keyout ${domain_cert_path}/privkey.pem \
            -out ${domain_cert_path}/fullchain.pem \
            -subj "/CN=localhost" certbot
    ;;
    create-test)
        certbot -n --nginx --agree-tos -m ${MAIL} -d test.${DOMAIN}
    ;;
    create-prod)
        #certbot -n --nginx --agree-tos -m ${MAIL} -d ${DOMAIN}
        #certbot -n --nginx --agree-tos -m ${MAIL} -d www.${DOMAIN}
        certbot -n --nginx --agree-tos -m ${MAIL} -d ${DOMAIN},www.${DOMAIN}
    ;;
    renew)
        certbot -n --nginx renew
    ;;
    *)
        echo "usage: $0 { localhost | create-test | create-prod | renew }"
        exit 1
    ;;
esac

exit 0

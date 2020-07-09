#!/usr/bin/env bash
# Copyright (C) 2020 art of coding UG, Hamburg

echo "Starting at $(date), domain ${domain}"
env
ls -l /etc/nginx/conf.d
cat /etc/nginx/nginx.conf
cat /etc/nginx/conf.d/*.conf
ls -lR ${tls.path}
nginx -t

if [ $# -gt 0 ]
then
    echo "Starting nginx with arguments (CMD)"
    exec "$@"
else
    echo "Starting nginx as daemon"
    nginx -g "daemon on;"
fi
echo "done"

domain_name="${domain}"
tld="${domain_name/*.}"
if [ ! -f /etc/letsencrypt/is_initialized ]
then
    echo "No TLS certificates exist, generating for domain ${tld}"
    if [ "${tld}" == "local" ]
    then
        echo "Creating self-signed TLS server certificates"
        selfsigned-openssl.sh ${hbk.hostname}
    elif [ -n "${domain}" ]
    then
        echo "Creating Let's Encrypt TLS server certificates"
        certonly_args="--agree-tos -m support@rootaid.de
            --webroot --webroot-path=/var/lib/letsencrypt
            --uir
            --hsts
            --staple-ocsp --must-staple
            -n"
        [ ! -d /var/lib/letsencrypt ] \
            && mkdir /var/lib/letsencrypt
        [ ! -d /etc/letsencrypt/live/${hbk.hostname} ] \
            && certbot certonly ${certonly_args} -d ${hbk.hostname}
        chmod 755 /etc/letsencrypt/archive
        chmod 755 /etc/letsencrypt/live
    else
        echo "Unknown domain, no TLS certificates generated"
    fi
    ls -lR ${tls.path}
    echo "done"
fi
touch /etc/letsencrypt/is_initialized

echo "Enabling servers: ${nginx.enable.servers}"
for server in ${nginx.enable.servers}
do
    if [ -f /etc/nginx/conf.d/${server}.conf.disabled ] && [ ! -f /etc/nginx/conf.d/${server}.conf ]
    then
        echo "Activating configuration for ${server}"
        mv /etc/nginx/conf.d/${server}.conf.disabled /etc/nginx/conf.d/${server}.conf
    fi
done

if [ "${tld}" != "local" ]
then
    echo "Starting crond to renew Lets Encrypt certificates"
    crond -b -S -l 8
    echo "done"
fi

echo "Restarting nginx"
nginx -s stop
sleep 1
nginx -g "daemon off;"

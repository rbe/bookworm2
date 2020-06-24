#!/usr/bin/env ash
# Copyright (C) 2020 art of coding UG, Hamburg

echo "Starting at $(date)"
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
if [ ! -f /etc/letsencrypt/is_initialized ]
then
    echo "No TLS certificates exist, generating for domain ${domain}"
    if [ "${domain_name/*.}" == "local" ]
    then
        echo "Creating self-signed TLS server certificates"
        selfsigned-openssl.sh ${hbk.hostname}
    else
        echo "Creating TLS server certificates"
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
    fi
    touch /etc/letsencrypt/is_initialized
    ls -lR ${tls.path}
    echo "done"
fi

for server in ${nginx.enable.servers}
do
    if [ -f /etc/nginx/conf.d/${server}.conf.disabled ] && [ ! -f /etc/nginx/conf.d/${server}.conf ]
    then
        echo "Activating configuration for ${server}"
        mv /etc/nginx/conf.d/${server}.conf.disabled /etc/nginx/conf.d/${server}.conf
    fi
done

if [ "${domain_name/*.}" != "local" ]
then
    echo "Starting crond to renew Lets Encrypt certificates"
    crond -b -S -l 8
    echo "done"
fi

echo "Restarting nginx"
nginx -s stop
sleep 1
nginx -g "daemon off;"

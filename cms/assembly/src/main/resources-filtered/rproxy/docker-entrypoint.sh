#!/usr/bin/env bash
# Copyright (C) 2020 art of coding UG, Hamburg

echo "Starting at $(date), domain ${domain}"
env
ls -l /etc/nginx/conf.d
cat /etc/nginx/nginx.conf
cat /etc/nginx/conf.d/*.conf
[[ -d "${tls.path}" ]] && ls -lR "${tls.path}"
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
if [[ ! -f /etc/letsencrypt/is_initialized ]]
then
  echo "No TLS certificates exist, generating for domain ${tld}"
  if [[ "${tld}" == "local" ]]
  then
    echo "Creating self-signed TLS server certificates"
    selfsigned-openssl.sh "${cms.hostname}"
  elif [[ -n "${domain}" ]]
  then
    echo "Creating Let's Encrypt TLS server certificates"
    certonly_args="--agree-tos -m support@rootaid.de
        --webroot --webroot-path=/var/lib/letsencrypt
        --uir
        --hsts
        --staple-ocsp --must-staple
        -n"
    if [[ ! -d /etc/letsencrypt/live/"${cms.hostname}" ]]; then
      echo "Generating new TLS certificate for ${cms.hostname}"
      [[ ! -d /var/lib/letsencrypt ]] && mkdir /var/lib/letsencrypt
      certbot certonly ${certonly_args} -d "${cms.hostname}"
    else
      echo "TLS certificate for ${cms.hostname} already generated"
    fi
    [[ -d /etc/letsencrypt/archive ]] && chmod 755 /etc/letsencrypt/archive
    [[ -d /etc/letsencrypt/live ]] && chmod 755 /etc/letsencrypt/live
  else
    echo "Unknown domain, no TLS certificates generated"
  fi
  if [[ -d "${tls.path}" ]]; then
    echo "Certificates in ${tls.path}"
    ls -lR "${tls.path}"
  fi
  echo "done"
  echo "Stopping nginx"
  nginx -s stop
  sleep 1
  echo "done"
  touch /etc/letsencrypt/is_initialized
fi

echo "Enabling servers: ${nginx.enable.servers}"
for server in ${nginx.enable.servers}
do
  if [ -f /etc/nginx/conf.d/"${server}".conf.disabled ] && [ ! -f /etc/nginx/conf.d/"${server}".conf ]
  then
    echo "Activating configuration for ${server}"
    mv /etc/nginx/conf.d/"${server}".conf.disabled /etc/nginx/conf.d/"${server}".conf
  fi
done

if [ "${tld}" != "local" ]
then
  echo "Starting crond to renew Lets Encrypt certificates"
  crond -b -S -l 8
  echo "done"
fi

echo "Starting nginx"
nginx -g "daemon off;"

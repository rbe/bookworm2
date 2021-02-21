#!/usr/bin/env bash
# Copyright (C) 2020 art of coding UG, Hamburg

echo "Starting at $(date), domain ${domain}"
env
ls -l /etc/nginx/conf.d
cat /etc/nginx/nginx.conf
for conf in $(find /etc/nginx/conf.d -type f -name \*.conf); do
  echo ""
  echo ""
  echo "Configuration ${conf}"
  cat "${conf}"
  echo ""
  echo ""
done
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
    selfsigned-openssl.sh "${keycloak.hostname}"
    selfsigned-openssl.sh "${hbk.hostname}"
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
    [[ ! -d /var/lib/letsencrypt ]] && mkdir /var/lib/letsencrypt
    [[ ! -d /etc/letsencrypt/live/${keycloak.hostname} ]] \
      && certbot certonly ${certonly_args} -d "${keycloak.hostname}"
    [[ ! -d /etc/letsencrypt/live/${hbk.hostname} ]] \
      && certbot certonly ${certonly_args} -d "${hbk.hostname}"
    [[ ! -d /etc/letsencrypt/live/${cms.hostname} ]] \
      && certbot certonly ${certonly_args} -d "${cms.hostname}"
    [[ -d /etc/letsencrypt/archive ]] && chmod 755 /etc/letsencrypt/archive
    [[ -d /etc/letsencrypt/live ]] && chmod 755 /etc/letsencrypt/live
  else
    echo "Unknown domain, no TLS certificates generated"
  fi
  if [[ -d "${tls.path}" ]]; then
    echo "TLS certificates in ${tls.path}"
    ls -lR "${tls.path}"
  fi
  echo "done"
  touch /etc/letsencrypt/is_initialized
fi

if [[ -n "${nginx.enable.servers}" ]]; then
  echo "Enabling servers: ${nginx.enable.servers}"
  for server in ${nginx.enable.servers}
  do
    if [ -f /etc/nginx/conf.d/"${server}".conf.disabled ] && [ ! -f /etc/nginx/conf.d/"${server}".conf ]
    then
      echo "Activating configuration for ${server}"
      mv /etc/nginx/conf.d/"${server}".conf.disabled /etc/nginx/conf.d/"${server}".conf
    elif [[ ! -f /etc/nginx/conf.d/"${server}".conf ]]; then
      echo "Configuration for ${server} already activated"
    fi
  done
fi

if [[ "${tld}" != "local" ]]
then
  echo "Starting crond to renew Lets Encrypt certificates"
  crond -b -S -l 8
  echo "done"
fi

echo "Stopping nginx"
nginx -s stop
sleep 1
echo "done"

echo "Starting nginx"
nginx -g "daemon off;"

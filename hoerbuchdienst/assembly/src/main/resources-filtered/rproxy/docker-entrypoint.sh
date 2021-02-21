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
    selfsigned-openssl.sh "${nginx.hostname}"
    selfsigned-openssl.sh "${portainer.hostname}"
    selfsigned-openssl.sh "${vault.hostname}"
    selfsigned-openssl.sh "${kes.hostname}"
    selfsigned-openssl.sh "${minio.hostname}"
    selfsigned-openssl.sh "${rabbitmq.hostname}"
    selfsigned-openssl.sh "${hbd.hostname}"
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
      [[ ! -d /etc/letsencrypt/live/"${nginx.hostname}" ]] \
          && certbot certonly ${certonly_args} -d "${nginx.hostname}"
      [[ ! -d /etc/letsencrypt/live/"${portainer.hostname}" ]] \
          && certbot certonly ${certonly_args} -d "${portainer.hostname}"
      [[ ! -d /etc/letsencrypt/live/"${vault.hostname}" ]] \
          && certbot certonly ${certonly_args} -d "${vault.hostname}"
      [[ ! -d /etc/letsencrypt/live/"${kes.hostname}" ]] \
          && certbot certonly ${certonly_args} -d "${kes.hostname}"
      [[ ! -d /etc/letsencrypt/live/${minio.hostname} ]] \
          && certbot certonly ${certonly_args} -d "${minio.hostname}"
      [[ ! -d /etc/letsencrypt/live/${rabbitmq.hostname} ]] \
          && certbot certonly ${certonly_args} -d "${rabbitmq.hostname}"
      [[ ! -d /etc/letsencrypt/live/${hbd.hostname} ]] \
          && certbot certonly ${certonly_args} -d "${hbd.hostname}"
      [[ -d /etc/letsencrypt/archive ]] && chmod 755 /etc/letsencrypt/archive
      chown 100:101 /etc/letsencrypt/archive/"${rabbitmq.hostname}"
      chown 100:101 /etc/letsencrypt/archive/"${rabbitmq.hostname}"/*
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

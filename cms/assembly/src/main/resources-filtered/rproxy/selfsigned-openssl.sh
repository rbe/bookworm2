#!/usr/bin/env ash
# Copyright (C) 2020 art of coding UG, Hamburg

set -o nounset
set -o errexit

if [ $# -ne 1 ]
then
    echo "usage: $0 <hostname>"
    exit 1
fi

HOSTNAME=$1
SELFSIGNED_PATH="${tls.path}"

echo "Generating self-signed TLS server certificate for ${HOSTNAME}"
mkdir -p ${SELFSIGNED_PATH}/${HOSTNAME}
openssl ecparam -genkey -name prime256v1 \
    | openssl ec -out ${SELFSIGNED_PATH}/${HOSTNAME}/privkey.pem
openssl req -new -x509 \
    -days 30 \
    -key ${SELFSIGNED_PATH}/${HOSTNAME}/privkey.pem \
    -out ${SELFSIGNED_PATH}/${HOSTNAME}/cert.pem \
    -subj "/C=/ST=/L=/O=/CN=${HOSTNAME}" \
    -addext "subjectAltName = IP:127.0.0.1,DNS:${HOSTNAME}"
cp ${SELFSIGNED_PATH}/${HOSTNAME}/cert.pem ${SELFSIGNED_PATH}/${HOSTNAME}/chain.pem
cp ${SELFSIGNED_PATH}/${HOSTNAME}/cert.pem ${SELFSIGNED_PATH}/${HOSTNAME}/fullchain.pem
chmod a+r ${SELFSIGNED_PATH}/${HOSTNAME}/*
echo "done"
ls -l ${SELFSIGNED_PATH}/${HOSTNAME}

exit 0

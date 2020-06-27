#!/usr/bin/env ash
# Copyright (C) 2020 art of coding UG, Hamburg

local="/var/local/kes"

if [ ! -f ${local}/is_initialized ]
then
    echo "Initializing MinIO KES"
    env | grep KES
    env | grep VAULT
    if [ ! -f ${local}/root.key ]
    then
        echo "Creating MinIO KES root key"
        kes tool identity new --key=${local}/root.key --cert=${local}/root.cert root
    fi
    root_identity="$(kes tool identity of ${local}/root.cert)"
    echo "Identity of root.cert: ${root_identity}"
    if [ ! -f ${local}/minio.key ]
    then
        echo "Creating MinIO KES minio key"
        kes tool identity new --key=${local}/minio.key --cert=${local}/minio.cert root
    fi
    minio_identity="$(kes tool identity of ${local}/minio.cert)"
    echo "Identity of minio: ${minio_identity}"
    approle_id="$(cat /vault/config/kes-role-id.json | jq -r .data.role_id)"
    approle_secret_id="$(cat /vault/config/kes-secret-id.json | jq -r .data.secret_id)"
    cat ${local}/server-config.tmpl.yml \
        | sed -e "s#ROOT_IDENTITY#${root_identity}#" \
              -e "s#APP_IDENTITY#${minio_identity}#" \
              -e "s#APPROLE_ID#${approle_id}#" \
              -e "s#APPROLE_SECRET_ID#${approle_secret_id}#" \
        >${local}/server-config.yml
fi

echo "Starting MinIO KES in background"
cat ${local}/server-config.yml
kes server \
    --mlock \
    --config=${local}/server-config.yml \
    --root "${root_identity}" \
    --auth=off \
    --key=${tls.path}/${kes.hostname}/privkey.pem \
    --cert=${tls.path}/${kes.hostname}/cert.pem &

if [ ! -f ${local}/is_initialized ]
then
    echo "Creating MinIO master key"
    sleep 5
    kes key create minio-masterkey-1 -k
    echo "done"
    touch ${local}/is_initialized
fi

tail -f /dev/null

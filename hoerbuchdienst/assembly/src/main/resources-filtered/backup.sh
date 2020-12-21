#!/usr/bin/env bash
#
# Copyright (C) 2018-2019 art of coding UG, https://www.art-of-coding.eu
# Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
# All rights reserved. Use is subject to license terms.
#

set -o nounset
set -o errexit

if [[ $# != 2 ]]; then
  echo "usage: $0 <env> <project>"
  echo "  env        dev | prod"
  echo "  project    hbk | hbd"
  exit 1
fi
env=$1
shift
project=$1
project_name="${env}-${project}"

mkdir -p ~/backup/"${project_name}"
pushd ~/backup/"${project_name}" >/dev/null

# Vault
mkdir -p vault
docker cp "${project_name}"_vault_1:/vault/config/vault-operator-init.txt vault
docker cp "${project_name}"_vault_1:/vault/config/kes-role-id.json vault
docker cp "${project_name}"_vault_1:/vault/config/kes-secret-id.json vault
# MinIO KES
mkdir -p kes
docker cp "${project_name}"_kes_1:/var/local/kes/minio.cert kes
docker cp "${project_name}"_kes_1:/var/local/kes/minio.key kes
docker cp "${project_name}"_kes_1:/var/local/kes/root.cert kes
docker cp "${project_name}"_kes_1:/var/local/kes/root.key kes
docker cp "${project_name}"_kes_1:/var/local/kes/server-config.yml kes
# MinIO
mkdir -p minio
docker cp "${project_name}"_minio_1:/var/local/minio/access_key minio
docker cp "${project_name}"_minio_1:/var/local/minio/secret_key minio
mkdir -p mc
docker cp "${project_name}"_mc_1:/var/local/mc/user_admin mc
docker cp "${project_name}"_mc_1:/var/local/mc/user_wbh mc

popd >/dev/null

exit 0

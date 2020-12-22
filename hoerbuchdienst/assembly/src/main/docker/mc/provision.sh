#!/usr/bin/env bash
# Copyright (C) 2020 art of coding UG, Hamburg

set -o nounset
set -o errexit

function recreate_user_readwrite() {
  local admin_conn="$1"
  shift
  local alias="$1"
  shift
  local roles=("$@")
  local aliasfile="/var/local/mc/user_${alias}"
  local access_key=""
  local secret_key=""
  if [[ -f /var/local/mc/user_"${alias}" ]]; then
    access_key="$(head -1 "${aliasfile}")"
    secret_key="$(tail -1 "${aliasfile}")"
  fi
  if [[ -z "${access_key}" ]]; then
    access_key="$(pwgen -BCn 20 1)"
  fi
  if [[ -z "${secret_key}" ]]; then
    secret_key="$(pwgen -BCn 40 1)"
  fi
  echo -e "${access_key}\n${secret_key}" >"${aliasfile}"
  mc admin user add "${admin_conn}" "${access_key}" "${secret_key}"
  echo "Access key=${access_key}"
  echo "Secret key=${secret_key}"
  for role in "${roles[@]}"; do
    mc admin policy set "${admin_conn}" "${role}" user="${access_key}"
  done
}

MINIO_URL="http://minio:9000"
MINIO_ACCESS_KEY="$(cat /var/local/minio/access_key)"
echo "MINIO_ACCESS_KEY=${MINIO_ACCESS_KEY}"
MINIO_SECRET_KEY="$(cat /var/local/minio/secret_key)"
echo "MINIO_SECRET_KEY=${MINIO_SECRET_KEY}"

echo "Configuring MinIO mc"
echo "Adding host minio:9000 with MinIO access and secret key"
mc config host add minio "${MINIO_URL}" "${MINIO_ACCESS_KEY}" "${MINIO_SECRET_KEY}" --api S3v4
echo "done"

echo "Adding policy userManager"
mc admin policy add minio userManager /var/local/mc/policy/userManager.json
echo "done"

echo "Creating admin user"
recreate_user_readwrite minio admin userManager
echo "done"
if [[ -f /var/local/mc/user_admin ]]; then
  echo "Adding host 'minio-admin'"
  ADMIN_ACCESS_KEY="$(head -1 /var/local/mc/user_admin)"
  ADMIN_SECRET_KEY="$(tail -1 /var/local/mc/user_admin)"
  mc config host add minio-admin "${MINIO_URL}" "${ADMIN_ACCESS_KEY}" "${ADMIN_SECRET_KEY}" --api S3v4
fi
echo "done"

echo "Creating WBH user"
recreate_user_readwrite minio-admin wbh readwrite
echo "done"
set +o errexit
#echo "Creating bucket 'rogers'"
#mc mb minio-admin/rogers
#echo "Creating bucket 'buckrogers'"
#mc mb minio-admin/buckrogers
echo "Creating bucket 'papierkorb'"
mc mb minio-admin/papierkorb
echo "Creating bucket 'hoerbuchdienst'"
mc mb minio-admin/hoerbuchdienst
echo "Creating bucket 'eingangskorb'"
mc mb minio-admin/eingangskorb
echo "Creating bucket 'ausgangskorb'"
mc mb minio-admin/ausgangskorb
echo "Creating bucket 'hoerbuchkatalog'"
mc mb minio-admin/hoerbuchkatalog
echo "Creating bucket 'hoererdaten'"
mc mb minio-admin/hoererdaten
set -o errexit
echo "done"

echo "Removing standard but unneeded host configurations"
set +o errexit
mc config host rm gcs
mc config host rm local
mc config host rm play
mc config host rm s3
set -o errexit
echo "done"

exit 0

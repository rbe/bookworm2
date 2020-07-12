#!/usr/bin/env bash
# Copyright (C) 2020 art of coding UG, Hamburg

set -o nounset
set -o errexit

function recreate_user_readwrite() {
  local admin_conn="$1"
  local alias="$1"
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
  mc admin policy set minio readwrite user="${access_key}"
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
ADMIN_ACCESS_KEY="$(pwgen -BCn 20 1)"
ADMIN_SECRET_KEY="$(pwgen -BCn 40 1)"
mc admin user add minio "${ADMIN_ACCESS_KEY}" "${ADMIN_SECRET_KEY}"
echo "Secret key=${ADMIN_SECRET_KEY}"
echo "done"
echo "Assigning user manager role to admin user"
mc admin policy set minio userManager user="${ADMIN_ACCESS_KEY}"
echo "done"
echo "Adding host 'minio-admin'"
mc config host add minio-admin "${MINIO_URL}" "${ADMIN_ACCESS_KEY}" "${ADMIN_SECRET_KEY}" --api S3v4
echo "done"

echo "Creating WBH user"
recreate_user_readwrite minio-admin wbh
echo "done"
echo "Creating WBH buckets"
set +o errexit
mc mb minio/rogers
mc mb minio/buckrogers
mc mb minio/hoerbuchdienst
mc mb minio/anlieferung
set -o errexit
echo "done"

exit 0

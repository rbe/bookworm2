#!/usr/bin/env bash
# Copyright (C) 2020 art of coding UG, Hamburg

# TODO Aus docker-entrypoint.sh starten
# TODO Nutzer werden wieder erzeugt, sofern /var/local/minio/nutzer_secret_key vorhanden ist

PROJECT_NAME="${docker.project.name}"

set -o nounset
set -o errexit

function recreate_user {
  local admin="$1"
  local access_key="$1"
  local secret_key
  secret_key="$(docker run --rm \
    -v "${PROJECT_NAME}"_miniolocal:/var/local/minio \
    alpine:latest \
    cat /var/local/minio/"${access_key}_secret" 2>/dev/null)"
  if [[ -z "${secret_key}" ]]; then
    secret_key="$(docker run --rm alpine:latest ash -c "apk add -q pwgen && pwgen -BCn 40 1")"
    docker run --rm \
      -v "${PROJECT_NAME}"_miniolocal:/var/local/minio \
      alpine:latest \
      echo "${secret_key}" >/var/local/minio/"${access_key}_secret"
  fi
  docker-compose -p "${PROJECT_NAME}" exec mc \
    mc admin user add "${admin}" "${access_key}" "${secret_key}"
}

MINIO_URL="http://minio:9000"
MINIO_ACCESS_KEY="$(docker run --rm \
    -v "${PROJECT_NAME}"_miniolocal:/var/local/minio \
    alpine:latest \
    cat /var/local/minio/access_key)"
echo "MINIO_ACCESS_KEY=${MINIO_ACCESS_KEY}"
MINIO_SECRET_KEY="$(docker run --rm \
    -v "${PROJECT_NAME}"_miniolocal:/var/local/minio \
    alpine:latest \
    cat /var/local/minio/secret_key)"
echo "MINIO_SECRET_KEY=${MINIO_SECRET_KEY}"

echo "Configuring MinIO mc"
echo "Adding host minio:9000 with MinIO access and secret key"
docker-compose -p "${PROJECT_NAME}" exec mc \
    mc config host add minio http://minio:9000 "${MINIO_ACCESS_KEY}" "${MINIO_SECRET_KEY}" --api S3v4
echo "Adding policy userManager"
docker-compose -p "${PROJECT_NAME}" exec mc \
    mc admin policy add minio userManager /var/local/mc/policy/userManager.json
echo "done"

echo "Creating user manager role"
docker-compose -p "${PROJECT_NAME}" exec mc \
    mc admin policy set minio userManager user=admin
echo "done"

echo "Creating admin user"
ADMIN_ACCESS_KEY="$(docker run --rm alpine:latest ash -c "apk add -q pwgen && pwgen -BCn 20 1")"
ADMIN_SECRET_KEY="$(docker run --rm alpine:latest ash -c "apk add -q pwgen && pwgen -BCn 40 1")"
docker-compose -p "${PROJECT_NAME}" exec mc \
    mc admin user add minio "${ADMIN_ACCESS_KEY}" "${ADMIN_SECRET_KEY}"
echo "Access key=${ADMIN_ACCESS_KEY}"
echo "Secret key=${ADMIN_SECRET_KEY}"
echo "done"

echo "Assign user manager role to admin user"
docker-compose -p "${PROJECT_NAME}" exec mc \
    mc config host add minio "${MINIO_URL}" "${ADMIN_ACCESS_KEY}" "${ADMIN_SECRET_KEY}" --api S3v4
echo "done"

echo "Creating WBH user"
WBH_ACCESS_KEY="$(docker run --rm alpine:latest ash -c "apk add -q pwgen && pwgen -BCn 20 1")"
WBH_SECRET_KEY="$(docker run --rm alpine:latest ash -c "apk add -q pwgen && pwgen -BCn 40 1")"
docker-compose -p "${PROJECT_NAME}" exec mc \
    mc admin user add minio-admin "${WBH_ACCESS_KEY}" "${WBH_SECRET_KEY}"
docker-compose -p "${PROJECT_NAME}" exec mc \
    mc admin policy set minio-admin readwrite user="${WBH_ACCESS_KEY}"
echo "Access key=${WBH_ACCESS_KEY}"
echo "Secret key=${WBH_SECRET_KEY}"
echo "done"

echo "Creating buckets"
set +o errexit
docker-compose -p "${PROJECT_NAME}" exec mc \
    mc mb minio-admin/rogers
docker-compose -p "${PROJECT_NAME}" exec mc \
    mc mb minio-admin/buckrogers
docker-compose -p "${PROJECT_NAME}" exec mc \
    mc mb minio-admin/hoerbuchdienst
docker-compose -p "${PROJECT_NAME}" exec mc \
    mc mb minio-admin/anlieferung
set -o errexit
echo "done"

exit 0

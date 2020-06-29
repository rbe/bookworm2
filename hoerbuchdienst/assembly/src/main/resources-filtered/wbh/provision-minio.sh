#!/usr/bin/env bash
# Copyright (C) 2020 art of coding UG, Hamburg

PROJECT_NAME="${docker.project.name}"

set -o nounset
set -o errexit

ADMIN_PASSWORD="Uuphuk3ayi7faghaik3wiekahnahxoo3oej4tee9"
WBH_ACCESS_KEY="Nae3bahcexie9eifaihe"
WBH_SECRET_KEY="eenieth9hoquohcooyeiRukoxopooPheew3caip7"
MINIO_URL="http://minio:9000"
MINIO_ACCESS_KEY="$(docker run --rm \
    -v ${PROJECT_NAME}_miniolocal:/var/local/minio \
    alpine:3.12 \
    cat /var/local/minio/access_key)"
echo "MINIO_ACCESS_KEY=${MINIO_ACCESS_KEY}"
MINIO_SECRET_KEY="$(docker run --rm \
    -v ${PROJECT_NAME}_miniolocal:/var/local/minio \
    alpine:3.12 \
    cat /var/local/minio/secret_key)"
echo "MINIO_SECRET_KEY=${MINIO_SECRET_KEY}"

echo "Configuring MinIO mc"
echo "Adding host minio:9000 with full access key"
docker-compose -p ${PROJECT_NAME} exec mc \
    mc config host add minio http://minio:9000 "${MINIO_ACCESS_KEY}" "${MINIO_SECRET_KEY}" --api S3v4
echo "Adding policy userManager"
docker-compose -p ${PROJECT_NAME} exec mc \
    mc admin policy add minio userManager /var/local/mc/policy/userManager.json
echo "done"

echo "Creating admin user"
docker-compose -p ${PROJECT_NAME} exec mc \
    mc admin user add minio admin "${ADMIN_PASSWORD}"
echo "done"

echo "Creating user manager role"
docker-compose -p ${PROJECT_NAME} exec mc \
    mc admin policy set minio userManager user=admin
docker-compose -p ${PROJECT_NAME} exec mc \
    mc config host add minio-admin "${MINIO_URL}" admin "${ADMIN_PASSWORD}" --api S3v4
echo "done"

echo "Creating WBH user"
docker-compose -p ${PROJECT_NAME} exec mc \
    mc admin user add minio-admin "${WBH_ACCESS_KEY}" "${WBH_SECRET_KEY}"
docker-compose -p ${PROJECT_NAME} exec mc \
    mc admin policy set minio-admin readwrite user="${WBH_ACCESS_KEY}"
docker-compose -p ${PROJECT_NAME} exec mc \
    mc config host add minio-wbh "${MINIO_URL}" "${WBH_ACCESS_KEY}" "${WBH_SECRET_KEY}"
echo "done"

echo "Creating buckets"
docker-compose -p ${PROJECT_NAME} exec mc \
    mc mb minio-wbh/rogers
docker-compose -p ${PROJECT_NAME} exec mc \
    mc mb minio-wbh/buckrogers
docker-compose -p ${PROJECT_NAME} exec mc \
    mc mb minio-wbh/hoerbuchdienst
echo "done"

exit 0

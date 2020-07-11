#!/usr/bin/env ash
# Copyright (C) 2020 art of coding UG, Hamburg

if [ -n "${MINIO_ACCESS_KEY_FILE}" ]; then
  MINIO_ACCESS_KEY="$(cat ${MINIO_ACCESS_KEY_FILE})"
  export MINIO_ACCESS_KEY
  if [ -n "${MINIO_SECRET_KEY_FILE}" ]; then
    MINIO_SECRET_KEY="$(cat ${MINIO_SECRET_KEY_FILE})"
    export MINIO_SECRET_KEY
  fi
else
  echo "Could not find MinIO access key"
fi

exec mc "$@"

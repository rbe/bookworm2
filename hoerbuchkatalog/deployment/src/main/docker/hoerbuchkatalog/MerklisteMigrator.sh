#!/usr/bin/env bash

JDBC_URL=jdbc:mysql://localhost:3306/bookworm
JDBC_USER=root
JDBC_SECRET=
REPO_BASE=./var

java -jar app/MerklisteMigrator.jar \
  ${JDBC_URL} ${JDBC_USER} ${JDBC_SECRET} ${REPO_BASE}

exit 0

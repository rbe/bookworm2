#!/usr/bin/env bash

if [[ $# -lt 1 ]]
then
    echo "usage: $0 <version>"
    exit 1
fi

set -o nounset
VERSION=$1

echo "*"
echo "* Building Docker image version ${VERSION}"
echo "*"
docker build \
    --tag wbh/hoerbuchkatalog:1 \
    --rm \
    -f Dockerfile-hoerbuchkatalog ..

if [[ $? -lt 0 ]]
then
    echo "Building image failed"
    exit 1
fi

echo "*"
echo "* Saving Docker image version ${VERSION}"
echo "*"
docker save \
    --output wbh-hoerbuchkatalog-${VERSION}.tar \
    wbh/hoerbuchkatalog:${VERSION}

if [[ $? -lt 0 ]]
then
    echo "Saving image failed"
    exit 1
fi

echo "* Done"
exit $?

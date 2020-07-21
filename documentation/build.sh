#!/usr/bin/env bash

if [[ $# != 1 ]]; then
  echo "usage: $0 <input file>"
  exit 1
fi

ASCIIDOCTOR_ARGS="--backend=html5 --doctype=book --safe-mode=unsafe --verbose --destination-dir=/var/local/destination"

set -o nounset
set -o errexit

docker build -t wbh-bookworm/documentation:1 src/main/docker

mkdir -p "$(pwd)/target/generated"
docker run \
  --rm \
  -it \
  --mount type=bind,src="$(pwd)/src/docs/asciidoc",dst=/var/local/asciidoc \
  --mount type=bind,src="$(pwd)/target/generated",dst=/var/local/destination \
  wbh-bookworm/documentation:1 \
  asciidoctor ${ASCIIDOCTOR_ARGS} /var/local/asciidoc/"$1"

exit 0

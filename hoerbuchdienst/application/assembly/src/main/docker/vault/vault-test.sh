#!/usr/bin/env bash
# Copyright (C) 2020 art of coding UG, Hamburg

set -o nounset
set -o errexit

vault_cli="docker-compose --no-ansi exec -e VAULT_ADDR=https://127.0.0.1:8200 -e VAULT_SKIP_VERIFY=1 vault vault"
${vault_cli} kv put secret/my-app/password password=123
${vault_cli} kv get secret/my-app/password
${vault_cli} kv get --format=json secret/my-app/password
${vault_cli} kv get -field=password secret/my-app/password
${vault_cli} kv list secret/
${vault_cli} kv delete secret/my-app/password

exit 0

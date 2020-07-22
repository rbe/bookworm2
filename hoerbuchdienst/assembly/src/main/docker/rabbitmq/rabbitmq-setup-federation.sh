#!/usr/bin/env bash
# Copyright (C) 2020 art of coding UG, Hamburg

set -o nounset
set -o errexit

if [[ $# -lt 1 ]]; then
  echo "usage: $0 <server1:user:pwd[ server2:user:pwd .. serverN:user:pwd]>"
  exit 1
fi

echo "Checking if RabbitMQ is online"
if rabbitmqctl await_startup; then
  echo "RabbitMQ appears to be online"
else
  echo "RabbitMQ is not online"
  exit 1
fi

# ALL_NODES should contain short hostname rabbitmq.shard<N>
# Naming convention for all shards:
#   rabbitmq.shard<N>.<same-shard_domain[.same-shard_domain]>.<tld>
ALL_NODES=("$@")
CONNECTION_PARAMS="heartbeat=10&connection_timeout=10000"

# Nodes
my_node_name="$(hostname -f)"
echo "My node name is ${my_node_name}"
shard_domain="$(hostname -d)"
shard_domain="${shard_domain##shard?.}"
echo "Common domain for all shards is ${shard_domain}"
for node in "${ALL_NODES[@]}"; do
  short_node_name="$(expr "${node}" : '\(.*\):.*:.*')"
  node_username="$(expr "${node}" : '.*:\(.*\):.*')"
  node_password="$(expr "${node}" : '.*:.*:\(.*\)')"
  upstream_node="${short_node_name}.${shard_domain}"
  upstream_name="${short_node_name/*./}"
  if [[ "${my_node_name}" != "${upstream_node}" ]]; then
    base_uri="amqps://${node_username}:${node_password}@${upstream_node}:5671/${MY_RABBITMQ_VHOST}"
    upstream_uri="${base_uri}"
    upstream_uri+="?server_name_indication=${upstream_node}"
    #upstream_uri+="&cacertfile=${tls.path}/${my_node_name}/chain.pem"
    #upstream_uri+="&certfile=${tls.path}/${my_node_name}/cert.pem"
    #upstream_uri+="&keyfile=${tls.path}/${my_node_name}/privkey.pem"
    #upstream_uri+="&verify=verify_peer"
    #upstream_uri+="&fail_if_no_peer_cert=true"
    [[ -n "${CONNECTION_PARAMS}" ]] && upstream_uri+="&${CONNECTION_PARAMS}"
    echo "Adding federation upstream to ${upstream_node} at ${base_uri}"
    rabbitmqctl set_parameter --vhost="${MY_RABBITMQ_VHOST}" \
      federation-upstream "${upstream_name}" "{\"uri\":\"${upstream_uri}\"}"
  else
    echo "Cannot add myself as upstream"
  fi
done

exit 0

#!/usr/bin/env bash
# Copyright (C) 2020 art of coding UG, Hamburg

# Naming convention: rabbitmq.shard<N>.<same domain>
ALL_NODES=("rabbitmq.shard1" "rabbitmq.shard2")
CONNECTION_PARAMS="heartbeat=10&connection_timeout=10000"

set -o nounset
set -o errexit

if [[ $# != 1 ]]; then
  echo "usage: $0 <username:password>"
  exit 1
fi

credentials="$1"
shift
username=${credentials/:*/}
password=${credentials/*:/}

# Nodes
my_node_name="$(hostname -f)"
domain="$(hostname -d)"
domain="${domain##shard?.}"
nodes=()
for node in "${ALL_NODES[@]}"; do
  nodes+=("${node}.${domain}")
done

# Upstreams
last_idx=$((${#nodes[@]} - 1))
for idx in $(seq 0 ${last_idx}); do
  upstream_node="${nodes[$idx]}"
  upstream_name="rabbitmq-shard$((idx + 1))"
  if [[ "${my_node_name}" != "${upstream_node}" ]]; then
    upstream_uri="amqps://${username}:${password}@${upstream_node}:5671/${MY_RABBITMQ_VHOST}"
    upstream_uri+="?server_name_indication=${upstream_node}"
    #upstream_uri+="&cacertfile=${tls.path}/${my_node_name}/chain.pem"
    #upstream_uri+="&certfile=${tls.path}/${my_node_name}/cert.pem"
    #upstream_uri+="&keyfile=${tls.path}/${my_node_name}/privkey.pem"
    #upstream_uri+="&verify=verify_peer"
    #upstream_uri+="&fail_if_no_peer_cert=true"
    [[ -n "${CONNECTION_PARAMS}" ]] && upstream_uri+="&${CONNECTION_PARAMS}"
    echo "Adding federation upstream to ${upstream_node} at ${upstream_uri}"
    rabbitmqctl set_parameter --vhost="${MY_RABBITMQ_VHOST}" \
      federation-upstream \
      "${upstream_name}" "{\"uri\":\"${upstream_uri}\"}"
  fi
done

## Policy
#echo "Setting policy for federated exchanges"
#rabbitmqctl set_policy --vhost="${MY_RABBITMQ_VHOST}" \
#    --priority 10 --apply-to exchanges \
#    federate-exchanges "^federated\." '{"federation-upstream-set" : "all"}'
#echo "Setting policy for federated queues"
#rabbitmqctl set_policy --vhost="${MY_RABBITMQ_VHOST}" \
#    --priority 10 --apply-to queues \
#    federate-queues "^federated\." '{"federation-upstream-set" : "all"}'

exit 0

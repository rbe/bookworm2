#!/usr/bin/env bash
# Copyright (C) 2020 art of coding UG, Hamburg

set -o nounset
set -o errexit

execdir="$(pushd "$(dirname "$0")" >/dev/null && pwd && popd >/dev/null)"
. "${execdir}/lib.sh"

pacinstall ufw

chmod 640 /etc/ufw/user.rules
chmod 640 /etc/ufw/before.rules
chmod 640 /etc/ufw/after.rules
chmod 640 /etc/ufw/user6.rules
chmod 640 /etc/ufw/before6.rules
chmod 640 /etc/ufw/after6.rules

echo "y" | ufw reset

ufw default deny incoming
ufw default deny outgoing

# DNS
ufw allow out domain/tcp
ufw allow out domain/udp
# NTP
ufw allow out ntp/tcp
ufw allow out ntp/udp
# SSH
ufw allow in from any to any port ssh proto tcp
ufw limit ssh
# HTTP outgoing: ArchLinux Mirrors
mirrors=("$(grep -E "^[^#.+]" /etc/pacman.d/mirrorlist | grep -Po 'http://\K[\w.-]+')")
for mirror in ${mirrors[*]}; do
  echo "Allowing HTTP to ArchLinux Mirror ${mirror}"
  ips=("$(getent hosts "${mirror}" | grep -Po '\K[a-f0-9:.]+ ')")
  for ip in ${ips[*]}; do
    ufw allow out from any to "${ip}" port http proto tcp
  done
done
# SSH and HTTP outgoing: GitHub, Bitbucket
repositories=("github.com" "bitbucket.org")
for repository in ${repositories[*]}; do
  echo "Allowing SSH and HTTPS to repository ${repository}"
  ips=("$(getent hosts "${repository}" | grep -Po '\K[a-f0-9:.]+ ')")
  for ip in ${ips[*]}; do
    ufw allow out from any to "${ip}" port ssh proto tcp
    ufw allow out from any to "${ip}" port https proto tcp
  done
done
# HTTP outgoing: Docker Registry
registries=("docker.io" "registry-1.docker.io" "registry.hub.docker.com" "production.cloudflare.docker.com")
for registry in ${registries[*]}; do
  echo "Allowing HTTPS to Docker Registry ${registry}"
  ips=("$(getent hosts "${registry}" | grep -Po '\K[a-f0-9:.]+ ')")
  for ip in ${ips[*]}; do
    ufw allow out from any to "${ip}" port https proto tcp
  done
done
# HTTP incoming
ufw allow in from any to any port http proto tcp
ufw allow in from any to any port https proto tcp
# HTTP incoming: RabbitMQ Web UI
ufw allow in from any to any port 15671 proto tcp
# RabbitMQ AMQPS
shard_names=("shard1" "shard2" "shard3" "shard4")
my_name="$(hostname -f)"
my_domain="$(hostname -d)"
for shard_name in ${shard_names[*]}; do
  fqdn="${shard_name}.${my_domain}"
  if [[ "${fqdn}" != "${my_name}" ]]; then
    echo "Allowing AMQPS from/to ${fqdn}"
    shard_ips=("$(getent hosts "${fqdn}" | grep -Po '\K[a-f0-9:.]+ ')")
    for shard_ip in ${shard_ips[*]}; do
      ufw allow in from "${shard_ip}" to any port 5671 proto tcp
      ufw allow out from any to "${shard_ip}" port 5671 proto tcp
    done
  fi
done

echo "y" | ufw enable
ufw status verbose
#ufw show raw

exit 0

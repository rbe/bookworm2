#!/usr/bin/env bash
# Copyright (C) 2020 art of coding UG, Hamburg

set -o nounset
set -o errexit

function zypperinstall() {
  local packages="$*"
  shift
  echo "Installing ${packages}"
  for package in ${packages[*]}; do
    if zypper install -y "${package}"; then
      echo "${package} installed successfully"
    else
      echo "${package} installation failed"
    fi
  done
}

function yuminstall() {
  local packages="$*"
  shift
  echo "Installing ${packages}"
  if yum -y install "${packages}"; then
    echo "${packages} installed successfully"
  else
    echo "${packages} installation failed"
  fi
}

function pacinstall() {
  local packages="$*"
  shift
  echo "Installing ${packages}"
  if pacman --noconfirm -S "${packages}"; then
    echo "${packages} installed successfully"
  else
    echo "${packages} installation failed"
  fi
}

function set_fqdn() {
  correct=0
  while [[ ${correct} != 1 ]]; do
    read -r -e -p "Please enter FQDN of server: " server_fqdn
    if [[ -n "${server_fqdn}" ]]; then
      server_name="${server_fqdn/.*/}"
      server_domain="${server_fqdn#*.}"
      echo "Server name is ${server_name}"
      echo "Servers's domain is ${server_domain}"
    fi
    if [[ -n "${server_fqdn}" ]]; then
      read -r -e -p "Is this correct? (Enter 'yes') " correct_answer
      [[ "${correct_answer}" == "yes" ]] && correct=1
      echo -e "${server_fqdn}" >/etc/hostname
      hostnamectl set-hostname "$(cat /etc/hostname)"
    else
      echo "No FQDN entered!"
      correct=0
    fi
    domains=("portainer" "vault" "kes" "s3" "rabbitmq" "hoerbuchdienst")
    for d in ${domains[*]}; do
      name="${d}-${server_fqdn}"
      if ! getent hosts "${name}"; then
        echo "${name} resolved: FAILED"
      else
        echo "${name} resolved: OK"
      fi
    done
  done
}

#!/usr/bin/env ash
# Copyright (C) 2020 art of coding UG, Hamburg

set -o nounset
set -o errexit

echo "Checking if RabbitMQ is online"
if rabbitmqctl await_startup; then
  echo   "RabbitMQ appears to be online"
else
  echo   "RabbitMQ is not online"
  exit   1
fi

echo "Adding virtual host ${MY_RABBITMQ_VHOST}"
rabbitmqctl add_vhost "${MY_RABBITMQ_VHOST}"
echo "done"

echo "Adding user federator"
federator_pw="$(pwgen -Bcn 32 -n 1)"
set +o errexit
rabbitmqctl add_user federator "${federator_pw}"
set -o errexit
echo "Password is ${federator_pw}"
rabbitmqctl change_password federator "${federator_pw}"
rabbitmqctl set_user_tags federator administrator
federator_configure_regexp=".*"
federator_write_regexp=".*"
federator_read_regexp=".*"
rabbitmqctl set_permissions -p "${MY_RABBITMQ_VHOST}" federator "${federator_configure_regexp}" "${federator_write_regexp}" "${federator_read_regexp}"
echo "done"

echo "Adding user bugs"
bugs_pw="$(pwgen -Bcn 32 -n 1)"
set +o errexit
rabbitmqctl add_user bugs "${bugs_pw}"
set -o errexit
echo "Password is ${bugs_pw}"
bugs_configure_regexp=""
bugs_write_regexp=".*"
bugs_read_regexp=".*"
rabbitmqctl set_permissions -p "${MY_RABBITMQ_VHOST}" bugs "${bugs_configure_regexp}" "${bugs_write_regexp}" "${bugs_read_regexp}"
echo "done"

echo "Deleting user guest"
set +o errexit
rabbitmqctl delete_user guest
set -o errexit
echo "done"
echo "Listing users"
rabbitmqctl list_users
echo "done"

echo "Deleting virtual host /"
rabbitmqctl delete_vhost /
echo "done"

exit 0

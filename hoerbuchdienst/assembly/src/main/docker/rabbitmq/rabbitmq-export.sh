#!/usr/bin/env bash
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

username="exporteur-$(pwgen -BCn 8 1)"
password="$(pwgen -BCn 32 1)"

if ! rabbitmqctl list_users | grep -c "${username}" >/dev/null; then
  rabbitmqctl add_user "${username}" "${password}"
fi
rabbitmqctl change_password "${username}" "${password}"
rabbitmqctl set_user_tags "${username}" administrator
configure_regexp=""
write_regexp=""
read_regexp=".*"
rabbitmqctl set_permissions \
  -p "${MY_RABBITMQ_VHOST}" \
  "${username}" "${configure_regexp}" "${write_regexp}" "${read_regexp}"

echo "Exporting RabbitMQ definitions"
my_name="$(hostname -f)"
rabbitmqadmin export \
  -s --host "${my_name}" --port 15671 \
  --username="${username}" --password="${password}" \
  --vhost="${MY_RABBITMQ_VHOST}" \
  /etc/rabbitmq/definitions.json
echo "done"

rabbitmqctl delete_user "${username}"

exit 0

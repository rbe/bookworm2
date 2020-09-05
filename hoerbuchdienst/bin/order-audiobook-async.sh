#!/usr/bin/env bash
# Copyright (C) 2020 art of coding UG, Hamburg

BASE_URL="http://localhost:8080"
MANDANT="WBH"
HOERERNUMMER="80170"
AGH_NUMMER=""
TITELNUMMER="32909"

set -o nounset
set -o errexit

echo "Ordering audiobook ${MANDANT}/${AGH_NUMMER}/${TITELNUMMER}"
curl -s -L -X POST -H 'Content-Type: application/json' --data "{
    \"mandant\": \"${MANDANT}\",
    \"hoerernummer\": \"${HOERERNUMMER}\",
    \"aghNummer\": \"${AGH_NUMMER}\",
    \"titelnummer\": \"${TITELNUMMER}\"
}" --output orderId.$$.txt "${BASE_URL}/bestellung/zip"
orderId="$(<orderId.$$.txt)"
echo "done, order ${orderId}"

orderStatus_url="${BASE_URL}/bestellung/zip/${TITELNUMMER}/status/${orderId}"
echo "Checking status for order ${orderId}"
orderStatus="$(curl -s -L -X GET "${orderStatus_url}")"
while [[ "${orderStatus}" != "SUCCESS" && "${orderStatus}" != "FAILED" ]]; do
  sleep 1
  orderStatus="$(curl -s -L -X GET "${orderStatus_url}")"
done
echo "done, status is ${orderStatus}"

if [[ "${orderStatus}" == "SUCCESS" ]]; then
  echo "Fetching audiobook as ZIP"
  curl -L -X GET --output hoerbuch.$$.zip "${BASE_URL}/bestellung/zip/${TITELNUMMER}/fetch/${orderId}"
  echo "done"
fi

exit 0

#!/bin/sh

TZ=Madrid/Europe
FECHA=$(date)
CURL_DATA=$(jq -n --arg sa "$FECHA" --arg an $1 '{alertname: $an, startsAt: $sa}')
#echo $CURL_DATA
curl -X POST -H "Content-Type: application/json" -d "$CURL_DATA"  $2

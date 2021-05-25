#!/bin/bash

TOPIC=$1
TOPIC_LOWERCASE=$(echo "$TOPIC" | awk '{print tolower($0)}')
KIBANA_IP_PORT=$2

USER=elastic
PASSWORD=changeme

fields_available="0"

while [ "$fields_available" == "0" ]
do
	echo "Trying to refresh ${TOPIC_LOWERCASE} index pattern"
    refresh_payload=$(curl -X GET -u ${USER}:${PASSWORD} "http://${KIBANA_IP_PORT}/api/index_patterns/_fields_for_wildcard?pattern=${TOPIC_LOWERCASE}&meta_fields=\[%22_source%22,%22_index%22,%22_id%22,%20%22_score%22,%20%22_type%22\]" | jq '.fields[] | . + {count: 0} | . + {scripted: false}' | jq -s  --arg TITLE "${TOPIC_LOWERCASE}" '. | tostring | {"attributes": {"title": $TITLE, "timeFieldName": "@timestamp", "fields": . }}')
    fields_available=$(echo $refresh_payload | grep log.file.path | wc -l)
    if [ "$fields_available" != "0" ]; then
       curl -X PUT -u ${USER}:${PASSWORD} "http://${KIBANA_IP_PORT}/api/saved_objects/index-pattern/index_${TOPIC_LOWERCASE}" -H 'kbn-xsrf: true' -H 'Content-Type: application/json' -d "$refresh_payload"
       sleep 5 
    fi
done

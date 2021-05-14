#!/bin/bash

TOPIC=$1
TOPIC_LOWERCASE=$(echo "$TOPIC" | awk '{print tolower($0)}')
ELASTICSEARCH_IP_PORT=$2
KAFKA_IP_PORT=$3
USER=elastic
PASSWORD=$4

echo "input { kafka { bootstrap_servers => '${KAFKA_IP_PORT}' client_id => 'logstash' topics => '$TOPIC' } } filter { json { source => \"message\" } } output { elasticsearch { hosts => \"${ELASTICSEARCH_IP_PORT}\" index => \"$TOPIC_LOWERCASE\" user => \"$USER\" password => \"$PASSWORD\" } }" | tee -a /etc/logstash/conf.d/${TOPIC}.conf > /dev/null;


# to show output: ... output { stdout { codec => rubydebug } elasticsearch ...
# input { kafka { bootstrap_servers => '${KAFKA_IP_PORT}' client_id => 'logstash' topics => '$TOPIC' } } filter { json { source => "message" } } output { stdout { codec => rubydebug } elasticsearch { hosts => \"${ELASTICSEARCH_IP_PORT}\" index => \"$TOPIC_LOWERCASE"\ user => \"$USER\" password => \"$PASSWORD\" } } | tee -a /etc/logstash/conf.d/${TOPIC}.conf > /dev/null;

echo $'- pipeline.id: '${TOPIC}$'\n  path.config: "/etc/logstash/conf.d/'${TOPIC}'.conf"' | tee -a /etc/logstash/pipelines.yml > /dev/null


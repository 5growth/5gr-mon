#!/bin/bash

create_kafka_topic_ip_port=$1
delete_kafka_topic_ip_port=$2
fetch_kafka_topic_ip_port=$3
logstash_pipeline_manager_ip_port=$4
elasticsearch_ip_address=$5
kafka_consumer_ip_port=$6
dashboard_manager_ip_address=$7
elastalert_ip_address=$8

# Start Log Pipeline Manager
echo "Start Log Pipeline Manager"
/usr/bin/python3 log_pipeline_manager_with_ip.py --create_kafka_topic_ip_port $create_kafka_topic_ip_port --delete_kafka_topic_ip_port $delete_kafka_topic_ip_port --fetch_kafka_topic_ip_port $fetch_kafka_topic_ip_port --log info --logstash_pipeline_manager_ip_port $logstash_pipeline_manager_ip_port --elasticsearch_ip_address $elasticsearch_ip_address --kafka_consumer_ip_port $kafka_consumer_ip_port --elk_password changeme --dashboard_manager_ip_address $dashboard_manager_ip_address --elastalert_ip_address $elastalert_ip_address > /var/log/log_pipeline_manager.log

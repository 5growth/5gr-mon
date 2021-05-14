#!/bin/bash

dashboard_manager_pod=$1
elastalert_pod=$2

# Start Log Pipeline Manager
echo "Start Log Pipeline Manager"
/usr/bin/python3 log_pipeline_manager_with_ip.py --create_kafka_topic_pod_port createkafkatopic:8190 --delete_kafka_topic_pod_port deletekafkatopic:8290 --fetch_kafka_topic_pod_port fetchkafkatopic:8390 --log info --logstash_pipeline_manager_pod_port logstashpipelinemanager:8191 --elasticsearch_pod elasticsearch --kafka_consumer_pod_port kafkaconsumer:8291 --elk_password changeme --dashboard_manager_pod $dashboard_manager_pod --elastalert_pod $elastalert_pod > /var/log/log_pipeline_manager.log


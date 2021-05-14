#!/bin/bash

create_kafka_topic_ip_address=`(sudo kubectl get pods -o wide | grep create-kafka-topic | awk '{print $6}')`
delete_kafka_topic_ip_address=`(sudo kubectl get pods -o wide | grep delete-kafka-topic | awk '{print $6}')`
fetch_kafka_topic_ip_address=`(sudo kubectl get pods -o wide | grep fetch-kafka-topic | awk '{print $6}')`
logstash_ip_address=`(sudo kubectl get pods -o wide | grep logstash-pipeline-manager | awk '{print $6}')`
elasticsearch_ip_address=`(sudo kubectl get pods -o wide | grep elasticsearch | awk '{print $6}')`
kafka_consumer_ip_address=`(sudo kubectl get pods -o wide | grep kafka-consumer | awk '{print $6}')`
kibana_dashboard_ip_address=`(sudo kubectl get pods -o wide | grep kibana-dashboard | awk '{print $6}')`
elastalert_ip_address=`(sudo kubectl get pods -o wide | grep elastalert | awk '{print $6}')`

sudo kubectl exec log_pipeline_manager -- /bin/bash entrypoint.sh $create_kafka_topic_ip_address:8190 $delete_kafka_topic_ip_address:8290 $fetch_kafka_topic_ip_address:8390 $logstash_ip_address:8191 $elasticsearch_ip_address $kafka_consumer_ip_address:8291 $kibana_dashboard_ip_address $elastalert_ip_address &

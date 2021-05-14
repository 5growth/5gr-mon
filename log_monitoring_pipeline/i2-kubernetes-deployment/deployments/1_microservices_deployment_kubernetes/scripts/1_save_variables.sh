#!/bin/bash

log_pipeline_manager_ip_address=`(sudo kubectl get pods -o wide | grep log-pipeline-manager | awk '{print $6}')`
create_kafka_topic_ip_address=`(sudo kubectl get pods -o wide | grep create-kafka-topic | awk '{print $6}')`
delete_kafka_topic_ip_address=`(sudo kubectl get pods -o wide | grep delete-kafka-topic | awk '{print $6}')`
elasticsearch_ip_address=`(sudo kubectl get pods -o wide | grep elasticsearch | awk '{print $6}')`
kafka_consumer_ip_address=`(sudo kubectl get pods -o wide | grep kafka-consumer | awk '{print $6}')`
kibana_dashboard_ip_address=`(sudo kubectl get pods -o wide | grep kibana-dashboard | awk '{print $6}')`
fetch_kafka_topic_ip_address=`(sudo kubectl get pods -o wide | grep fetch-kafka-topic | awk '{print $6}')`
kafka_ip_address=`(sudo kubectl get pods -o wide | grep '^kafka ' | awk '{print $6}')`
kibana_ip_address=`(sudo kubectl get pods -o wide | grep '^kibana ' | awk '{print $6}')`
logstash_ip_address=`(sudo kubectl get pods -o wide | grep logstash-pipeline-manager | awk '{print $6}')`
zookeeper_ip_address=`(sudo kubectl get pods -o wide | grep zookeeper | awk '{print $6}')`
elastalert_ip_address=`(sudo kubectl get pods -o wide | grep elastalert | awk '{print $6}')`
http_server_ip_address=`(sudo kubectl get pods -o wide | grep http-server | awk '{print $6}')`

echo $log_pipeline_manager_ip_address
echo $create_kafka_topic_ip_address
echo $delete_kafka_topic_ip_address
echo $elasticsearch_ip_address
echo $kafka_consumer_ip_address
echo $fetch_kafka_topic_ip_address
echo $kafka_ip_address
echo $kibana_ip_address
echo $kibana_dashboard_ip_address
echo $logstash_ip_address
echo $zookeeper_ip_address
echo $elastalert_ip_address
echo $http_server_ip_address

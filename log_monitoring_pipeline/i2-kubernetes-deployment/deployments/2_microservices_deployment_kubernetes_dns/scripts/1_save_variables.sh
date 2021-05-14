#!/bin/bash


log_pipeline_pod=$(sudo kubectl get pods -o wide | grep logpipeline | awk '{print $1}')
create_pod=$(sudo kubectl get pods -o wide | grep create | awk '{print $1}')
delete_pod=$(sudo kubectl get pods -o wide | grep delete | awk '{print $1}')
elasticsearch_pod=$(sudo kubectl get pods -o wide | grep elasticsearch | awk '{print $1}')
kafka_consumer_pod=$(sudo kubectl get pods -o wide | grep kafkaconsumer | awk '{print $1}')
kibana_dashboard_pod=$(sudo kubectl get pods -o wide | grep kibanadashboard- | awk '{print $1}') 
fetch_pod=$(sudo kubectl get pods -o wide | grep fetch | awk '{print $1}')
kafka_pod=$(sudo kubectl get pods -o wide | grep kafka- | awk '{print $1}')
kibana_pod=$(sudo kubectl get pods -o wide | grep kibana-| awk '{print $1}')
logstash_pod=$(sudo kubectl get pods -o wide | grep logstash | awk '{print $1}')
zookeeper_pod=$(sudo kubectl get pods -o wide | grep zookeeper | awk '{print $1}')
elastalert_pod=$(sudo kubectl get pods -o wide | grep elastalert | awk '{print $1}')
http_server_pod=$(sudo kubectl get pods -o wide | grep httpserver | awk '{print $1}')

echo $log_pipeline_pod
echo $create_pod
echo $delete_pod
echo $elasticsearch_pod
echo $kafka_consumer_pod
echo $kibana_dashboard_pod
echo $fetch_pod
echo $kafka_pod
echo $kibana_pod
echo $logstash_pod
echo $zookeeper_pod
echo $elastalert_pod
echo $http_server_pod

# In case you need them:

#log_pipeline_manager_ip_address=`(sudo kubectl get pods -o wide | grep logpipelinemanager | awk '{print $6}')`
#create_kafka_topic_ip_address=`(sudo kubectl get pods -o wide | grep createkafkatopic | awk '{print $6}')`
#delete_kafka_topic_ip_address=`(sudo kubectl get pods -o wide | grep deletekafkatopic | awk '{print $6}')`
#elasticsearch_ip_address=`(sudo kubectl get pods -o wide | grep elasticsearch | awk '{print $6}')`
#kafka_consumer_ip_address=`(sudo kubectl get pods -o wide | grep kafkaconsumer | awk '{print $6}')`
# This one to be modified when Kibana dashboard is moved to DNS
#kibana_dashboard_ip_address=`(sudo kubectl get pods -o wide | grep kibana-dashboard | awk '{print $6}')`
#fetch_kafka_topic_ip_address=`(sudo kubectl get pods -o wide | grep fetchkafkatopic | awk '{print $6}')`
#kafka_ip_address=`(sudo kubectl get pods -o wide | grep kafka- | awk '{print $6}')`
# This one to be modified when Kibana is moved to DNS
#kibana_ip_address=`(sudo kubectl get pods -o wide | grep '^kibana ' | awk '{print $6}')`
#logstash_ip_address=`(sudo kubectl get pods -o wide | grep logstashpipelinemanager | awk '{print $6}')`
#zookeeper_ip_address=`(sudo kubectl get pods -o wide | grep zookeeper | awk '{print $6}')`
#elastalert_ip_address=`(sudo kubectl get pods -o wide | grep elastalert | awk '{print $6}')`
#http_server_ip_address=`(sudo kubectl get pods -o wide | grep http-server | awk '{print $6}')`

#echo $log_pipeline_manager_ip_address
#echo $create_kafka_topic_ip_address
#echo $delete_kafka_topic_ip_address
#echo $elasticsearch_ip_address
#echo $kafka_consumer_ip_address
#echo $kibana_dashboard_ip_address
#echo $fetch_kafka_topic_ip_address
#echo $kafka_ip_address
#echo $kibana_ip_address
#echo $logstash_ip_address
#echo $zookeeper_ip_address
#echo $elastalert_ip_address
#echo $http_server_ip_address

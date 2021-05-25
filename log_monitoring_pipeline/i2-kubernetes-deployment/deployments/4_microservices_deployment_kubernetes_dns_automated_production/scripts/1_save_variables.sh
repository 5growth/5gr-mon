#!/bin/bash

log_pipeline_pod=$(sudo kubectl get pods -o wide | grep logpipeline | awk '{print $1}')
create_pod=$(sudo kubectl get pods -o wide | grep create | awk '{print $1}')
delete_pod=$(sudo kubectl get pods -o wide | grep delete | awk '{print $1}')
elasticsearch_pod=$(sudo kubectl get pods -o wide | grep elasticsearch | awk '{print $1}')
kafka_consumer_pod=$(sudo kubectl get pods -o wide | grep kafkaconsumer | awk '{print $1}')
kibana_dashboard_pod=$(sudo kubectl get pods -o wide | grep kibana-dashboard- | awk '{print $1}') 
fetch_pod=$(sudo kubectl get pods -o wide | grep fetch | awk '{print $1}')
kibana_pod=$(sudo kubectl get pods -o wide | grep '^kibana-' | awk '{print $1}' | grep -v dashboard)
logstash_pod=$(sudo kubectl get pods -o wide | grep logstash | awk '{print $1}')
elastalert_pod=$(sudo kubectl get pods -o wide | grep elastalert | awk '{print $1}')

echo $log_pipeline_pod
echo $create_pod
echo $delete_pod
echo $elasticsearch_pod
echo $kafka_consumer_pod
echo $kibana_dashboard_pod
echo $fetch_pod
echo $kibana_pod
echo $logstash_pod
echo $elastalert_pod

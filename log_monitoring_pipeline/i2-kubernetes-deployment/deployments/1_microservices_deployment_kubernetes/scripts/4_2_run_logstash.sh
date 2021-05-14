#!/bin/bash

elasticsearch_ip_address=`(sudo kubectl get pods -o wide | grep elasticsearch | awk '{print $6}')`
kafka_ip_address=`(sudo kubectl get pods -o wide | grep '^kafka ' | awk '{print $6}')`

sudo kubectl exec logstash_pipeline_manager -- /bin/bash entrypoint.sh \"http://$elasticsearch_ip_address:9200\" $kafka_ip_address:9092 $elasticsearch_ip_address:9200 &

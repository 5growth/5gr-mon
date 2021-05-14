#!/bin/bash

kibana_ip_address=`(sudo kubectl get pods -o wide | grep '^kibana ' | awk '{print $6}')`
kafka_ip_address=`(sudo kubectl get pods -o wide | grep '^kafka ' | awk '{print $6}')`

sudo kubectl exec kafka_consumer -- /bin/bash entrypoint.sh $kibana_ip_address:5601 $kafka_ip_address:9092 &

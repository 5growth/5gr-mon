#!/bin/bash

kafka_ip_address=`(sudo kubectl get pods -o wide | grep '^kafka ' | awk '{print $6}')`

sudo kubectl exec fetch_kafka_topic -- /bin/bash entrypoint.sh $kafka_ip_address:9092 &

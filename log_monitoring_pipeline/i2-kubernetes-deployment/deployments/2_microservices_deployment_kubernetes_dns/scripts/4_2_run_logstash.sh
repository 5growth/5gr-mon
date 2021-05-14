#!/bin/bash

logstash_pod=$(sudo kubectl get pods -o wide | grep logstash | awk '{print $1}')

sudo kubectl exec $logstash_pod -- /bin/bash entrypoint.sh \"http://elasticsearch:9200\" kafka:9092 elasticsearch:9200 &

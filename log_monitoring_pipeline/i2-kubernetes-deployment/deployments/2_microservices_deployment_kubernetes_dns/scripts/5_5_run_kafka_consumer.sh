#!/bin/bash

kafka_consumer_pod=$(sudo kubectl get pods -o wide | grep kafkaconsumer | awk '{print $1}')

sudo kubectl exec $kafka_consumer_pod -- /bin/bash entrypoint.sh kibana:5601 kafka:9092 &

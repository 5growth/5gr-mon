#!/bin/bash

kafka_pod=$(sudo kubectl get pods -o wide | grep kafka- | awk '{print $1}')

sudo kubectl exec $kafka_pod -- /bin/bash entrypoint.sh PLAINTEXT://0.0.0.0:9092 PLAINTEXT://kafka:9092 1 zookeeper > /dev/null &

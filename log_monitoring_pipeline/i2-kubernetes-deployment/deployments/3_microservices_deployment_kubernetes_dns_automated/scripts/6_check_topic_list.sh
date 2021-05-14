#!/bin/bash

kafka_pod=$(sudo kubectl get pods -o wide | grep kafka- | awk '{print $1}')

sudo kubectl exec $kafka_pod -- /bin/bash /opt/kafka/bin/kafka-topics.sh --list --zookeeper zookeeper:2181

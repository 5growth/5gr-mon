#!/bin/bash

zookeeper_ip_address=`(sudo kubectl get pods -o wide | grep zookeeper | awk '{print $6}')`

sudo kubectl exec kafka -- /bin/bash entrypoint.sh PLAINTEXT://0.0.0.0:9092 PLAINTEXT://dcm:9092 1 $zookeeper_ip_address > /dev/null &

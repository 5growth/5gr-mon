#!/bin/bash

zookeeper_ip_address=`(sudo kubectl get pods -o wide | grep zookeeper | awk '{print $6}')`

sudo kubectl exec kafka -- /bin/bash /opt/kafka/bin/kafka-topics.sh --list --zookeeper $zookeeper_ip_address:2181 &

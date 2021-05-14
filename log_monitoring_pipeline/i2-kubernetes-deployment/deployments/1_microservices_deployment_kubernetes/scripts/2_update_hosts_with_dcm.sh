#!/bin/bash

kafka_ip_address=`(sudo kubectl get pods -o wide | grep '^kafka ' | awk '{print $6}')`

sudo kubectl exec kafka -- /bin/bash update_hosts.sh $kafka_ip_address dcm
sudo kubectl exec logstash_pipeline_manager -- /bin/bash update_hosts.sh $kafka_ip_address dcm
sudo kubectl exec create_kafka_topic -- /bin/bash update_hosts.sh $kafka_ip_address dcm
sudo kubectl exec delete_kafka_topic -- /bin/bash update_hosts.sh $kafka_ip_address dcm
sudo kubectl exec fetch_kafka_topic -- /bin/bash update_hosts.sh $kafka_ip_address dcm
sudo kubectl exec kafka_consumer -- /bin/bash update_hosts.sh $kafka_ip_address dcm

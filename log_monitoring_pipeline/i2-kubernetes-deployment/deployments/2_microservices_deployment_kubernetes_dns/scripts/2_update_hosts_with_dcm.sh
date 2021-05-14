#!/bin/bash

# This script is not needed in this scenario, but it is kept here for future usages.

sudo kubectl exec $kafka_pod -- /bin/bash update_hosts.sh 10.9.8.188 dcm
sudo kubectl exec $logstash_pod -- /bin/bash update_hosts.sh 10.9.8.188 dcm
sudo kubectl exec $create_pod -- /bin/bash update_hosts.sh 10.9.8.188 dcm
sudo kubectl exec $delete_pod -- /bin/bash update_hosts.sh 10.9.8.188 dcm
sudo kubectl exec $fetch_pod -- /bin/bash update_hosts.sh 10.9.8.188 dcm
sudo kubectl exec $kafka_consumer_pod -- /bin/bash update_hosts.sh 10.9.8.188 dcm


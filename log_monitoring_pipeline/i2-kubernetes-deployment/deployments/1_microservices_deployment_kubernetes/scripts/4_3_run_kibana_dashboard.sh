#!/bin/bash
# Arguments:
# 1-Host IP

HOST_IP=$1
kibana_ip_address=`(sudo kubectl get pods -o wide | grep '^kibana ' | awk '{print $6}')`
elasticsearch_ip_address=`(sudo kubectl get pods -o wide | grep elasticsearch | awk '{print $6}')`

sudo kubectl exec kibana_dashboard -- /bin/bash entrypoint.sh $kibana_ip_address $elasticsearch_ip_address $HOST_IP

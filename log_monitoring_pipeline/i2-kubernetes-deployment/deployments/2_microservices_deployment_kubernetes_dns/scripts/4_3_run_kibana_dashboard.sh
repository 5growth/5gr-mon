#!/bin/bash
# Arguments:
# 1-Host IP

HOST_IP=$1
kibana_dashboard_pod=$(sudo kubectl get pods -o wide | grep kibanadashboard- | awk '{print $1}')

sudo kubectl exec $kibana_dashboard_pod -- /bin/bash entrypoint.sh kibana elasticsearch $HOST_IP

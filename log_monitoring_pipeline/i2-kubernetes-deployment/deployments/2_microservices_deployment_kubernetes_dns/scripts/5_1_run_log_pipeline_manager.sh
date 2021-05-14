#!/bin/bash

log_pipeline_pod=$(sudo kubectl get pods -o wide | grep logpipeline | awk '{print $1}')

sudo kubectl exec $log_pipeline_pod -- /bin/bash entrypoint.sh kibanadashboard elastalert &

#!/bin/bash

kibana_pod=$(sudo kubectl get pods -o wide | grep kibana- | awk '{print $1}')

sudo kubectl exec $kibana_pod -- /bin/bash entrypoint.sh "http://elasticsearch:9200"

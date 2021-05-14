#!/bin/bash

elasticsearch_pod=$(sudo kubectl get pods -o wide | grep elasticsearch | awk '{print $1}')

sudo kubectl exec $elasticsearch_pod -- /bin/bash entrypoint.sh &

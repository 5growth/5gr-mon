#!/bin/bash

fetch_pod=$(sudo kubectl get pods -o wide | grep fetch | awk '{print $1}')

sudo kubectl exec $fetch_pod -- /bin/bash entrypoint.sh kafka:9092 &

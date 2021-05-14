#!/bin/bash

create_pod=$(sudo kubectl get pods -o wide | grep create | awk '{print $1}')

sudo kubectl exec $create_pod -- /bin/bash entrypoint.sh kafka:9092 &

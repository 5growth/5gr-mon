#!/bin/bash

delete_pod=$(sudo kubectl get pods -o wide | grep delete | awk '{print $1}')

sudo kubectl exec $delete_pod -- /bin/bash entrypoint.sh kafka:9092 &

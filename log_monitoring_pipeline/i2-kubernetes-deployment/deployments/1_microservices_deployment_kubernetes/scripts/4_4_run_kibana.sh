#!/bin/bash

elasticsearch_ip_address=`(sudo kubectl get pods -o wide | grep elasticsearch | awk '{print $6}')`

sudo kubectl exec kibana -- /bin/bash entrypoint.sh "http://$elasticsearch_ip_address:9200"

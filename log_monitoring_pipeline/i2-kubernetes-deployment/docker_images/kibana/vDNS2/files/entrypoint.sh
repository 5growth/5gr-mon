#!/bin/bash
elasticsearch_hosts=$1

# Modify Kibana configuration that depends on environment variables
sed -i -e "s/#server.host: \"localhost\"/server.host: \"0.0.0.0\"/" /etc/kibana/kibana.yml
sed -i -e "s;#elasticsearch.hosts: \[\"http://localhost:9200\"\];elasticsearch.hosts: [$elasticsearch_hosts];" /etc/kibana/kibana.yml

# Start Kibana
/etc/init.d/kibana start
tail -f /etc/kibana/kibana.yml

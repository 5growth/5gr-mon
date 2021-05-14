#!/bin/bash
elasticsearch_hosts=$1
kafka_ip_port=$2
elasticsearch_ip_port=$3

# Modify Logstash configuration that depends on environment variables
sed -i -e "s;#xpack.monitoring.elasticsearch.hosts: \[\"https://es1:9200\", \"https://es2:9200\"\];xpack.monitoring.elasticsearch.hosts: [$elasticsearch_hosts];" /etc/logstash/logstash.yml

# Start Logstash
/bin/bash /usr/bin/logstash_pipeline_manager/run_logstash.sh

# Start Logstash Pipeline Manager
echo "Start Logstash Pipeline Manager"
/usr/bin/python3 /usr/bin/logstash_pipeline_manager/logstash_pipeline_manager.py --script_path /usr/bin/logstash_pipeline_manager --kafka_ip_port $kafka_ip_port --elasticsearch_ip_port $elasticsearch_ip_port --elk_password changeme --log info > /var/log/logstash_pipeline_manager.log


#!/bin/bash

source /etc/default/logstash
echo "Starting Logstash"
/usr/share/logstash/bin/logstash "--path.settings" "/etc/logstash" > /var/log/logstash.log &
#echo $! > /tmp/logstash.pid

echo "Started Logstash. PID:"
ps aux | grep -i logstash | awk NR==1{'print $2'} > /tmp/logstash.pid
cat /tmp/logstash.pid

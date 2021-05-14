#!/bin/bash

# Start Elasticsearch
/etc/init.d/elasticsearch start
tail -f /var/log/elasticsearch/log_pipeline_manager.log

# Docker images related to the microservices scenario

In this folder, you can find the Docker images related to the microservices scenario, in which the 5GROWTH Monitoring Platform - Log Monitoring Pipeline is decoupled in different microservices. This is a previous step before deploying the platform using serverless functions.

## Structure of the repository

Each folder contains a specific Docker image for a specific building block of the Monitoring Platform - Log Monitoring Pipeline architecture. The folders are the following:

* **[create_kafka_topic](create_kafka_topic):** it contains the Docker image related to the Create Kafka Topic microservice.
* **[delete_kafka_topic](delete_kafka_topic):** it contains the Docker image related to the Delete Kafka Topic microservice.
* **[elastalert](elastalert):** it contains the Docker image related to ElastAlert.
* **[elasticsearch](elasticsearch):** it contains the Docker image related to Elasticsearch.
* **[fetch_kafka_topic](fetch_kafka_topic):** it contains the Docker image related to the Fetch Kafka Topic microservice.
* **[kafka](kafka):** it contains the Docker image related to Kafka.
* **[kafka_consumer](kafka_consumer):** it contains the Docker image related to the Kafka Consumer microservice.
* **[kibana](kibana):** it contains the Docker image related to Kibana
* **[kibana_dashboards](kibana_dashboards):** it contains the Docker image related to the Java logic that manages the dashboards.
* **[log_pipeline_manager](log_pipeline_manager):** it contains the Docker image related to the Log Pipeline Manager.
* **[logstash_pipeline_manager](logstash_pipeline_manager):** it contains the Docker image related to Logstash, also including a Python logic managing the pipeline's configuration.
* **[zookeeper](zookeeper):** it contains the Docker image related to ZooKeeper.

Images related to the 5G EVE scenario are placed in [old](old) folder.

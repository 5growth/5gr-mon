# Log Pipeline Manager Docker image

Docker image containing the Log Pipeline Manager, which triggers the execution of other microservices and the creation of a Kibana Dashboard and alerts in ElastAlert.

## Build the image

```sh
$ docker build -t log_pipeline_manager:v1 .
```

## Run the image

```sh
$ docker run --name <container_name> -p 8987:8987 -t -d log_pipeline_manager:v1
```

Where:

* **container_name:** name for the container to be deployed.

## Configure the Container

Run the following command in the container:

```sh
$ docker exec -it <container_name> /bin/bash entrypoint.sh $create_kafka_topic_ip_port $delete_kafka_topic_ip_port $fetch_kafka_topic_ip_port $logstash_pipeline_manager_ip_port $elasticsearch_ip_address $kafka_consumer_ip_port $dashboard_manager_ip_address $elastalert_ip_address
```

Where:

* **create_kafka_topic_ip_port:** IP:port of Create Kafka Topic (localhost:8190 by default, but must be changed with the final IP address used in the Create Kafka Topic container).
* **delete_kafka_topic_ip_port:** IP:port of Delete Kafka Topic (localhost:8290 by default, but must be changed with the final IP address used in the Delete Kafka Topic container).
* **fetch_kafka_topic_ip_port:** IP:port of Fetch Kafka Topic (localhost:8390 by default, but must be changed with the final IP address used in the Fetch Kafka Topic container).
* **logstash_pipeline_manager_ip_port:** IP:port of Logstash Pipeline Manager (localhost:8191 by default, but must be changed with the final IP address used in the Logstash Pipeline Manager container).
* **elasticsearch_ip_address:** IP of Elasticsearch (localhost by default, but must be changed with the final IP address used in the Elasticsearch container).
* **kafka_consumer_ip_port:** IP:port of Kafka Consumer (localhost:8291 by default, but must be changed with the final IP address used in the Kafka Consumer container).
* **dashboard_manager_ip_address:** IP of Dashboard manager (localhost by default, but must be changed with the final IP address used in the Dashboard manager container).
* **elastalert_ip_address:** IP of ElastAlert (localhost by default, but must be changed with the final IP address used in the ElastAlert container).

## Checking the correct deployment of the service

By executing the following command, the correct deployment of the service can be checked. In case of not receiving a message, it means that everything went fine.

```sh
$ curl --location --request GET 'http://<container_ip>:8987'
```

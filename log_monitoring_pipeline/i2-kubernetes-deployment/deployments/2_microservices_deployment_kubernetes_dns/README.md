# 2. Microservices deployment with DNS (Kubernetes)

This README file contains all the steps to be followed to deploy this scenario, based on Kubernetes, in which it is presented the Log Monitoring platform based on microservices. It also includes Filebeat, but it is executed in the same server, and not as a pod. Kubernetes DNS utility is used to manage the connection between containers.

![Architecture](img/monitoring_architecture_2.png)

## Docker images involved

The following Docker images have been used for this deployment. Please verify that these images have been built beforehand.

> Use --no-cache option when building the images if you find problems with the apt-get update command.

* **Create Kafka Topic:** available in this repository: [create_kafka_topic](../../docker_images/create_kafka_topic).
* **Delete Kafka Topic:** available in this repository: [delete_kafka_topic](../../docker_images/delete_kafka_topic).
* **ElastAlert:** available in this repository: [elastalert](../../docker_images/elastalert/v2.6_DNS).
* **Elasticsearch:** available in this repository: [elasticsearch](../../docker_images/elasticsearch/original).
* **Fetch Kafka Topic:** available in this repository: [fetch_kafka_topic](../../docker_images/fetch_kafka_topic).
* **HTTP server:** available in this repository: [kafka](../../docker_images/http_server).
* **Kafka:** available in this repository: [kafka](../../docker_images/kafka/original).
* **Kafka Consumer:** available in this repository: [kafka_consumer](../../docker_images/kafka_consumer).
* **Kibana:** available in this repository: [kibana](../../docker_images/kibana/original).
* **Kibana Dashboard:** available in this repository: [kibana_dashboards](../../docker_images/kibana_dashboards/original).
* **Log Pipeline Manager:** available in this repository: [log_pipeline_manager](../../docker_images/log_pipeline_manager/vDNS).
* **Logstash Pipeline Manager:** available in this repository: [logstash_pipeline_manager](../../docker_images/logstash_pipeline_manager/v2).
* **ZooKeeper:** available in this repository: [zookeeper](../../docker_images/zookeeper/v2).

## Steps to be followed

For the moment, it is necessary to create a namespace where all pods will be launched.

In order to do so, execute the following code lines:  

```sh
$ kubectl create namespace dns-deployment2
$ kubectl config set-context --current --namespace=dns-deployment2
```
### 1. Run all pods

Before running the pods, check the following:

* The Kubernetes node uses Docker as container daemon.
* You have built all the Docker images referenced in pods' specification.

Then, execute the following (you have to be in the directory containing this README to execute these commands).

> For the ElastAlert pod, the file pods/config_map/elastalert_pod_config_map.yml can be alternatively used if you want to have all ElastAlert server parameters included in the same pod definition file.  

```sh
$ kubectl apply -f ./pods/create_kafka_topic_pod.yml
$ kubectl apply -f ./pods/delete_kafka_topic_pod.yml
$ kubectl apply -f ./pods/elastalert_pod.yml
$ kubectl apply -f ./pods/elasticsearch_pod.yml
$ kubectl apply -f ./pods/fetch_kafka_topic_pod.yml
$ kubectl apply -f ./pods/http_server_pod.yml
$ kubectl apply -f ./pods/kafka_pod.yml
$ kubectl apply -f ./pods/kafka_consumer_pod.yml
$ kubectl apply -f ./pods/kibana_pod.yml
$ kubectl apply -f ./pods/kibana_dashboard_pod.yml
$ kubectl apply -f ./pods/log_pipeline_manager_pod.yml
$ kubectl apply -f ./pods/logstash_pipeline_manager_pod.yml
$ kubectl apply -f ./pods/zookeeper_pod.yml
```

Or execute all in just one command:

```sh
$ kubectl apply -f pods
```

After this, take note of pods' IP addresses by running this:

```sh
$ kubectl get pods -o wide
```

You should obtain something like this:

```
NAME                                       READY   STATUS    RESTARTS   AGE   IP           NODE             NOMINATED NODE   READINESS GATES
kibanadashboard-749fcc6fdd-djbm4           1/1     Running   0          10s   10.42.0.74   log-monitoring   <none>           <none>
createkafkatopic-6c47b64b45-vt87z          1/1     Running   0          11s   10.42.0.66   log-monitoring   <none>           <none>
kafka-75589fb56d-8cnlx                     1/1     Running   0          11s   10.42.0.72   log-monitoring   <none>           <none>
httpserver-cf567b948-trxw6                 1/1     Running   0          11s   10.42.0.71   log-monitoring   <none>           <none>
logstashpipelinemanager-58c65bf758-fgk9n   1/1     Running   0          10s   10.42.0.77   log-monitoring   <none>           <none>
fetchkafkatopic-5bf58f47b5-lvfq9           1/1     Running   0          11s   10.42.0.69   log-monitoring   <none>           <none>
kibana-65b9f7b848-bk44c                    1/1     Running   0          10s   10.42.0.75   log-monitoring   <none>           <none>
logpipelinemanager-59c98488d6-9qd6q        1/1     Running   0          10s   10.42.0.76   log-monitoring   <none>           <none>
zookeeper-74b54ff96-6874v                  1/1     Running   0          10s   10.42.0.78   log-monitoring   <none>           <none>
elastalert-66d9645959-x2sn2                1/1     Running   0          11s   10.42.0.70   log-monitoring   <none>           <none>
deletekafkatopic-cb5c499dd-x27l7           1/1     Running   0          11s   10.42.0.67   log-monitoring   <none>           <none>
elasticsearch-67f6d96f45-9cg7m             1/1     Running   0          11s   10.42.0.68   log-monitoring   <none>           <none>
kafkaconsumer-5455568dfb-pn568             1/1     Running   0          11s   10.42.0.73   log-monitoring   <none>           <none>
```

You can also check the services deployed with this command:

```sh
$ kubectl get services -o wide
```

You should obtain something like this:

```
NAME                      TYPE        CLUSTER-IP      EXTERNAL-IP   PORT(S)    AGE   SELECTOR
kubernetes                ClusterIP   10.43.0.1       <none>        443/TCP    11d   <none>
createkafkatopic          ClusterIP   10.43.98.72     <none>        8190/TCP   77s   run=createkafkatopic
deletekafkatopic          ClusterIP   10.43.109.0     <none>        8290/TCP   77s   run=deletekafkatopic
elastalert                ClusterIP   10.43.147.77    <none>        3030/TCP   77s   run=elastalert
elasticsearch             ClusterIP   10.43.60.208    <none>        9200/TCP   77s   run=elasticsearch
fetchkafkatopic           ClusterIP   10.43.44.43     <none>        8390/TCP   77s   run=fetchkafkatopic
httpserver                ClusterIP   10.43.252.30    <none>        8000/TCP   77s   run=httpserver
kafkaconsumer             ClusterIP   10.43.147.202   <none>        8291/TCP   77s   run=kafkaconsumer
kafka                     ClusterIP   10.43.13.91     <none>        9092/TCP   77s   run=kafka
kibanadashboard           ClusterIP   10.43.181.214   <none>        8080/TCP   77s   run=kibanadashboard
kibana                    ClusterIP   10.43.146.194   <none>        5601/TCP   77s   run=kibana
logpipelinemanager        ClusterIP   10.43.144.233   <none>        8987/TCP   77s   run=logpipelinemanager
logstashpipelinemanager   ClusterIP   10.43.198.65    <none>        8191/TCP   77s   run=logstashpipelinemanager
zookeeper                 ClusterIP   10.43.51.23     <none>        2181/TCP   77s   run=zookeeper
```

To use the correct container names in the following scripts, you can save their names by using the following script.

> **This script must be executed in every single terminal being used**

```sh
$ source scripts/1_save_variables.sh
```

> **The rest of proposed scripts should be executed in different terminals to better see the output**

### 2. Configure all pods to use kafka as Kafka IP address

As the containers in the namespace know how to resolve the 'kafka' name with the Kubernetes DNS, this step is not needed to be executed.

### 3. Configure Kafka pod

```sh
$ /bin/bash scripts/3_run_kafka.sh
```

And also, you can check in ZooKeeper pod that Kafka has correctly joined to ZooKeeper:

```sh
$ kubectl exec -it $zookeeper_pod -- /opt/kafka/bin/zookeeper-shell.sh zookeeper:2181 ls /brokers/ids
```

The broker_id will appear on the terminal. In this case, the broker_id was set to "1" and therefore a [1] appears on the screen, like this:

```
Connecting to zookeeper:2181

WATCHER::

WatchedEvent state:SyncConnected type:None path:null
[1]
```

### 4. Configure the ELK Stack with ElastAlert

> ElastAlert does not need additional scripts to run properly.

First of all, start by running Elasticsearch:

```sh
$ /bin/bash scripts/4_1_run_elasticsearch.sh
```

Then, run Logstash. Wait until Logstash is running (it uses a dummy pipeline to keep it running).

```sh
$ /bin/bash scripts/4_2_run_logstash.sh
```

After this, run Kibana dashboards. It will take a while to compile the jar files. Wait until the logs are stopped. You have to change <node_containing_kibana_pod_ip_address> with the proper IP.

```sh
$ /bin/bash scripts/4_3_run_kibana_dashboard.sh <node_containing_kibana_pod_ip_address>
```

Finally, run Kibana. Just wait until the service is ready (you can see this in the browser in <node_containing_kibana_pod_ip_address>:5601). 

```sh
$ /bin/bash scripts/4_4_run_kibana.sh
```

## 5. Deploy microservices

Then, it is time to create the microservices related to the serverless functions.

First of all, start by running the Log Pipeline Manager.

> Log Pipeline Manager image configures right now the deployment of the whole stack in a unique machine. In order to deploy Kibana and ElastAlert in two separate machines, you should change the content of [5_1_run_log_pipeline_manager.sh](scripts/5_1_run_log_pipeline_manager.sh), replacing kibanadashboard and elastalert with the IPs of the servers in which these containers have been deployed.

```sh
$ /bin/bash scripts/5_1_run_log_pipeline_manager.sh
```

Then, the Create Kafka Topic service:

```sh
$ /bin/bash scripts/5_2_run_create_kafka_topic.sh
```

Next, the Delete Kafka Topic service:

```sh
$ /bin/bash scripts/5_3_run_delete_kafka_topic.sh
```

Continue with the Fetch Kafka Topic service:

```sh
$ /bin/bash scripts/5_4_run_fetch_kafka_topic.sh
```

And finally, deploy the Kafka Consumer service.

```sh
$ /bin/bash scripts/5_5_run_kafka_consumer.sh
```

Up to this point of the deployment, the whole Log Pipeline Manager stack has been launched. Then, all pods are running and configured in order to start the workflow: topic creation, dashboard creation, alert creation.

### 6. Create a new topic from the Config Manager side

Create a new topic in the platform. Use the IP address of the node that contains the Log Pipeline Manager pod, changing *<node_containing_log_pipeline_manager_pod_ip_address>* as a result. The variable *<topic_name>*, also known as nsId, must be set before sending the request.

> You can also execute this request with the POST Create Topic request from the [Postman collection](test/Requests.json). _Remember to change the IP address and topic name in that case._ 

```sh
$ curl --location --request POST 'http://<node_containing_log_pipeline_manager_pod_ip_address>:8987/job/<topic_name>' \
--header 'Content-Type: application/json' 
```

If you list the topics currently created, you will see that <topic_name> has been created.

```sh
$ /bin/bash scripts/6_check_topic_list.sh
```

### 7. Create Kibana Dashboard from the Config Manager side

Create a new dashboard for the already created topic in the platform. Use the IP address of the node that contains the Log Pipeline Manager pod, changing *<node_containing_log_pipeline_manager_pod_ip_address>* as a result. The variable *<topic_name>*, also known as nsId, must be set before sending the request.

> You can also execute this request with the POST Create Dashboard request from the [Postman collection](test/Requests.json). _Remember to change the IP address and topic name in that case._ 

```sh
$ curl --location --request POST 'http://<node_containing_log_pipeline_manager_pod_ip_address>:8987/kibanaDashboard' \
--header 'Content-Type: application/json' \
--data-raw '{
"dashboardTitle": "NS_<topic_name>",
"ns_id": "<topic_name>",
"dashboard_type": "vm_logs"
}'
```

Kibana will respond with the <dashboardId> parameter, which will be later on used to delete the dashboard, so keep it saved.

```
{
  "dashboardId":"<dashboardId>",
  "dashboardTitle":"NS_<topic_name>",
  "dashboard_type":"vm_logs",
  "ns_id":"<topic_name>",
  "url":"http://<node_containing_kibana_pod_ip_address>:5601/app/kibana#/dashboard/<dashboardId>?embed=true&_g=(refreshInterval:(pause:!f,value:10000))"
}
```

### 8. Post ElastAlert alert rule to ElastAlert server's REST API

Create a new Elastalert rule for the already created topic in the platform. Use the IP address of the node that contains the Log Pipeline Manager pod, changing *<node_containing_log_pipeline_manager_pod_ip_address>* as a result. The variable *<topic_name>*, also known as nsId, must be set before sending the request.

You also have to change the content of *query* field, to put the condition to be met to trigger the alarm. For example, put there a "*3*", so that if the message contains a 3, it triggers the alarm.

> You can also execute this request with the POST Create Alert request from the [Postman collection](test/Requests.json). _Remember to change the IP address, the IP address of the target and the topic name in that case._ 

```sh
$ curl --location --request POST 'http://<node_containing_log_pipeline_manager_pod_ip_address>:8987/alert' \
--header 'Content-Type: application/json' \
--data-raw '{
"alertName": "alert_name",
"labels": [],
"query": "*3*",
"severity": "warning",
"for": "20s",
"target": "httpserver:8000/alert_receiver",
"kind": "match",
"index": "<topic_name>"
}'
```

This alert rule instructs Elastalert server to monitor Elasticsearch index's frequency and to send alert to the http-server by running an empty curl command.

The response should be like the following one. Take note of the *<alert_id>* provided, because it will be used afterwards for deleting the rule.

```
{
  "alertId":"<alertId>",
  "alertName":"alert_name",
  "for":"20s",
  "index":"<topic_name>",
  "kind":"match",
  "labels":[],
  "query":"*3*",
  "severity":"warning",
  "target":"httpserver:8000/alert_receiver"
}
```

Finally, to check that the rule has been correctly generated, you can check if the file has been created in the corresponding directory in Elastalert pod, together with its content (after checking the <alertId> generated).

```sh
$ kubectl exec -it $elastalert_pod -- ls ../elastalert/rules
$ kubectl exec -it $elastalert_pod -- cat ../elastalert/rules/<alertId>.yaml
```

> TBC: LogScraper

### 9. Run Filebeat

Follow the following steps to install Filebeat in a specific server (if not installed previously):

```sh
$ cd /tmp
$ wget https://artifacts.elastic.co/downloads/beats/filebeat/filebeat-7.5.0-amd64.deb
$ dpkg -i /tmp/filebeat-7.5.0-amd64.deb
$ rm /tmp/filebeat-7.5.0-amd64.deb

# Check that Filebeat service is stopped (we will execute it manually)
$ systemctl stop filebeat
```

Then, remove the Filebeat configuration file provided by default:

```sh
$ rm /etc/filebeat/filebeat.yml
```

Create a new */etc/filebeat/filebeat.yml* with the following content, changing *<node_containing_kafka_pod_ip_address>*  and <topic_name> consequently. 

```
filebeat.inputs:
  - type: log
    paths:
      - /var/log/<topic_name>.log
output.kafka:
  hosts: ["<node_containing_kafka_pod_ip_address>:9092"]
  topic: "<topic_name>"
```

Do not forget to include an entry in the /etc/hosts file in Filebeat's server with the following content, changing *<node_containing_kafka_pod_ip_address>* consequently:

```
<node_containing_kafka_pod_ip_address> kafka
```

And finally, in a different terminal, check that the configuration file provided before is correct and run Filebeat:

```sh
$ filebeat test config
$ filebeat test output
$ filebeat -e -d "publish" # alternative: systemctl start filebeat -> and check the logs with journalctl -fu filebeat
```

### 10. Start server receiving alerts

Since http-server pod is already running a http server, just execute in a different terminal the following command:

```sh
$ watch -n 5 sudo kubectl logs $http_server_pod
```

It will appear listening to requests on the port 8000 every 5 seconds. This port is used when creating the alert in Step 8. The target was defined on its IP:port, and therefore after generating data in Step 11, an Alert will appear on this terminal.

### 11. Generate data to be published by Filebeat

Open a new terminal in the server containing Filebeat and execute the following commands. After this, you will start publishing data to Filebeat (10 metrics, 1 per second). Remember to change <topic_name> consequently.

```sh
$ sudo su
$ for i in {1..10}; do timestamp=$(date +"%s"); echo "$i"  >> /var/log/<topic_name>.log; sleep 1; done
```

In the terminal containing the HTTP server logs, you will receive then a request from ElastAlert, as the condition defined for the rule has been achieved. If you have a look at the HTTP server terminal, something like this should've appeared.

```
04/05/2021 14:27:52 INFO Request received - POST /alert_receiver
04/05/2021 14:27:52 INFO Data received: {'startsAt': 'Tue May  4 14:27:52 UTC 2021', 'alertname': 'c023539a-ace4-11eb-b80c-e2b576bed96e'}
```

Moreover, check that the system receives the messages sent by the publisher (you can go to the Kibana GUI with http://<node_containing_kibana_pod_ip_address>:5601 and take a look to the Kibana index receiving the data, the Kibana dashboard generated, the Elasticsearch index increasing the counter of messages received, etc.).

### 12. (Optional) Check messages received in a Kafka consumer

You can also run the subscriber in order to confirm that it receives the messages sent by the publisher (i.e. Filebeat).

Within the Kafka pod, there is a script called kafka-console-consumer.sh which, given the <topic_name>, it shows every single message received in that topic from the beginning to the present time, being continously updated. 

```sh
$ kubectl exec -it $kafka_pod -- /bin/bash
$ cd /opt/kafka
$ bin/kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic <topic_name> --from-beginning
```

### 13. Delete the Alert created by the Config Manager

> BEFORE THIS: delete logscraper

Send the following request. Remember to change <alertId> with the one obtained when the rule was created.

> You can also execute this request with the DELETE Delete Alert request from the [Postman collection](test/Requests.json). _Remember to change the IP address and the topic name (the topic name, in this case, has to be changed with the alertId) in that case._ 

```sh
$ curl --location --request DELETE 'http://<node_containing_log_pipeline_manager_pod_ip_address>:8987/alert/<alertId>' \
--header 'Content-Type: application/json'
```

### 14. Delete the Dashboard created by the Config Manager

Send the following request, using the <dashboardId> you obtained in step 7.

> You can also execute this request with the DELETE Delete Dashboard request from the [Postman collection](test/Requests.json). _Remember to change the IP address and dashboardId (NOT topic_name) in that case._ 

```sh
$ curl --location --request DELETE 'http://<node_containing_log_pipeline_manager_pod_ip_address>:8987/kibanaDashboard/<dashboardId>' \
--header 'Content-Type: application/json'
```

### 15. Delete the topic created by the Config Manager

Remove the topic created previously by the Config Manager by sending this request from the Config Manager side, changing *<node_containing_log_pipeline_manager_pod_ip_address>* and *<topic_name>* as a result.

> You can also execute this request with the DELETE Delete Topic request from the [Postman collection](test/Requests.json). _Remember to change the IP address and topic name in that case._ 

```sh
$ curl --location --request DELETE 'http://<node_containing_log_pipeline_manager_pod_ip_address>:8987/job/<topic_name>' \
--header 'Content-Type: application/json'
```

If you list the topics currently created, you will see that <topic_name> has been deleted.

```sh
$ /bin/bash scripts/6_check_topic_list.sh
```

### 16. Cleaning the scenario

> TO BE REVIEWED THIS - After this, you can close Filebeat (or execute *systemctl stop filebeat* if you used the other alternative), ElastAlert and the HTTP server in the terminals opened for these purposes.

To clean the scenario, you can execute the following commands:

```sh
$ kubectl delete -f ./pods/create_kafka_topic_pod.yml
$ kubectl delete -f ./pods/delete_kafka_topic_pod.yml
$ kubectl delete -f ./pods/elastalert_pod.yml
$ kubectl delete -f ./pods/elasticsearch_pod.yml
$ kubectl delete -f ./pods/fetch_kafka_topic_pod.yml
$ kubectl delete -f ./pods/http_server_pod.yml
$ kubectl delete -f ./pods/kafka_pod.yml
$ kubectl delete -f ./pods/kafka_consumer_pod.yml
$ kubectl delete -f ./pods/kibana_pod.yml
$ kubectl delete -f ./pods/kibana_dashboard_pod.yml
$ kubectl delete -f ./pods/log_pipeline_manager_pod.yml
$ kubectl delete -f ./pods/logstash_pipeline_manager_pod.yml
$ kubectl delete -f ./pods/zookeeper_pod.yml
```

Also remove Filebeat log file for future executions:

```sh
$ rm /var/log/<topic_name>.log
```

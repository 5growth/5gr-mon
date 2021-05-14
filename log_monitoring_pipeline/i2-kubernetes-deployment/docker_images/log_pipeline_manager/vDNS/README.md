# Log Pipeline Manager Docker image

Docker image containing the Log Pipeline Manager, which triggers the execution of other microservices and the creation of a Kibana Dashboard and alerts in ElastAlert.

## Build the image

```sh
$ docker build -t log_pipeline_manager:vDNS .
```

## Run the image

```sh
$ docker run --name <container_name> -p 8987:8987 -t -d log_pipeline_manager:vDNS
```

Where:

* **container_name:** name for the container to be deployed.

## Configure the Container

Run the following command in the container:

```sh
$ docker exec -it <container_name> /bin/bash entrypoint.sh $dashboard_manager_pod $elastalert_pod
```

Where:

* **dashboard_manager_pod:** IP/pod name of Dashboard manager (localhost by default, but must be changed with the final IP address/pod name used in the Dashboard manager container).
* **elastalert_pod:** IP/pod name of ElastAlert (localhost by default, but must be changed with the final IP address/pod name used in the ElastAlert container).

## Checking the correct deployment of the service

By executing the following command, the correct deployment of the service can be checked. In case of not receiving a message, it means that everything went fine.

```sh
$ curl --location --request GET 'http://<container_ip>:8987'
```

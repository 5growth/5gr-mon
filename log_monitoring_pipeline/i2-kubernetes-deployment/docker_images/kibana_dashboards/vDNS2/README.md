# Kibana Dashboards Docker image

Docker image containing the dashboards' handler and PostgreSQL to save the dashboards' data.

## Build the image

```sh
$ docker build -t kibana_dashboard:vDNS2 .
```

## Run the image

```sh
$ docker run --name <container_name> -p 8080:8080 -t -d kibana_dashboard:vDNS2
```

Where:

* **container_name:** name for the container to be deployed.

## Configure the Container

Run the following command in the container:

```sh
$ docker exec -it <container_name> /bin/bash entrypoint.sh $kibana_ip_address $elasticsearch_ip_address $host_ip_address
```

* **kibana_ip_address:** IP address of Kibana (localhost by default, but must be changed with the final IP address used in the Kibana container).
* **elasticsearch_ip_address:** IP address of Elasticsearch (localhost by default, but must be changed with the final IP address used in the Kibana container).
* **host_ip_address:** IP address of the host in which this container is running (localhost by default, but must be changed with the IP to be used).

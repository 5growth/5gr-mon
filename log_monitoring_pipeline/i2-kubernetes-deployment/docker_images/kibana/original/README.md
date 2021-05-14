# Kibana Docker image

Docker image containing Kibana, from the ELK Stack.

## Build the image

```sh
$ docker build -t kibana .
```

## Run the image

```sh
$ docker run --name <container_name> -p 5601:5601 -t -d kibana
```

Where:

* **container_name:** name for the container to be deployed.

## Configure the Container

Run the following command in the container:

```sh
$ docker exec -it <container_name> /bin/bash entrypoint.sh $elasticsearch_hosts 
```

* **elasticsearch_hosts:** hosts that belongs to the Elasticsearch cluster, written as a list (e.g. \"host1\", \"host2\"...).
